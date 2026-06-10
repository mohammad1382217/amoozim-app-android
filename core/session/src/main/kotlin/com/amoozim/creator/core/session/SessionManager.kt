package com.amoozim.creator.core.session

import com.amoozim.creator.core.common.result.ApiResult
import com.amoozim.creator.core.model.JwtCallbackRequest
import com.amoozim.creator.core.model.JwtCallbackResponse
import com.amoozim.creator.core.model.JwtRefreshRequest
import com.amoozim.creator.core.model.Profile
import com.amoozim.creator.core.network.NetworkCaller
import com.amoozim.creator.core.network.auth.RefreshOutcome
import com.amoozim.creator.core.network.auth.TokenRefresher
import com.amoozim.creator.core.session.data.AuthBootstrapApi
import com.amoozim.creator.core.session.data.ProfileApi
import com.amoozim.creator.core.session.data.TokenStore
import dagger.Lazy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single source of truth for the authenticated session. Owns the [AuthState] machine,
 * the current [Profile], and the mini-app context, and implements [TokenRefresher] so
 * the network layer's 401 authenticator can drive a single-flight refresh here.
 *
 * `ProfileApi` is injected lazily ([Lazy]) to break the construction cycle:
 * authed Retrofit → authenticator → [TokenRefresher] (this) → ProfileApi → authed Retrofit.
 */
@Singleton
class SessionManager @Inject constructor(
    private val tokenStore: TokenStore,
    private val authBootstrapApi: AuthBootstrapApi,
    private val networkCaller: NetworkCaller,
    private val profileApi: Lazy<ProfileApi>,
) : TokenRefresher {

    private val _state = MutableStateFlow(
        if (tokenStore.hasSession()) AuthState.Authenticated else AuthState.Idle,
    )
    val state: StateFlow<AuthState> = _state.asStateFlow()

    private val _profile = MutableStateFlow<Profile?>(null)
    val profile: StateFlow<Profile?> = _profile.asStateFlow()

    private val _roleAccess = MutableStateFlow<RoleAccess>(RoleAccess.from(null))
    val roleAccess: StateFlow<RoleAccess> = _roleAccess.asStateFlow()

    val miniAppId: String get() = tokenStore.miniAppId

    private val refreshLock = Any()

    /**
     * The faithful login: exchange an Eitaa `initData` blob for a JWT pair via
     * `POST auth/jwt/callback`. On a standalone install the [initData] is supplied by
     * the host bridge or, in dev, pasted into the entry screen.
     */
    suspend fun bootstrapWithInitData(
        miniAppId: String,
        initData: String,
        deviceId: String?,
    ): ApiResult<JwtCallbackResponse> {
        tokenStore.setContext(miniAppId.trim(), deviceId?.trim()?.ifBlank { null })
        _state.value = AuthState.Authenticating

        val result = networkCaller.call {
            authBootstrapApi.callback(tokenStore.miniAppId, tokenStore.deviceId, JwtCallbackRequest(initData))
        }
        when (result) {
            is ApiResult.Success -> {
                val data = result.data
                if (data.accessToken.isNotBlank()) {
                    tokenStore.commitTokens(data.accessToken, data.refreshToken)
                    setProfile(data.user)
                    _state.value = AuthState.Authenticated
                } else {
                    _state.value = AuthState.Unauthenticated
                }
            }
            is ApiResult.Error -> _state.value = AuthState.Unauthenticated
        }
        return result
    }

    /**
     * Dev/QA shortcut: seed an existing JWT pair directly, skipping the callback
     * exchange. Useful for exercising the UI when no Eitaa host is available.
     */
    fun bootstrapWithTokens(miniAppId: String, accessToken: String, refreshToken: String, deviceId: String?) {
        tokenStore.setContext(miniAppId.trim(), deviceId?.trim()?.ifBlank { null })
        tokenStore.commitTokens(accessToken.trim(), refreshToken.trim())
        _state.value = AuthState.Authenticated
    }

    /** Hydrates the current user from `auth/me` (e.g. on warm start when only tokens persist). */
    suspend fun refreshProfile(): ApiResult<Profile> {
        val result = networkCaller.call { profileApi.get().me() }
        if (result is ApiResult.Success) setProfile(result.data)
        return result
    }

    fun signOutLocally() {
        tokenStore.clear()
        setProfile(null)
        _state.value = AuthState.Unauthenticated
    }

    // --- TokenRefresher (called by the OkHttp authenticator, on the network thread) ---

    override fun refreshBlocking(staleAccessToken: String?): RefreshOutcome = synchronized(refreshLock) {
        val current = tokenStore.accessToken()
        // Another request already refreshed while we waited on the lock.
        if (!current.isNullOrBlank() && current != staleAccessToken) {
            return RefreshOutcome.Success(current)
        }
        val refreshToken = tokenStore.refreshToken()
        if (refreshToken.isNullOrBlank()) {
            handleTerminal()
            return RefreshOutcome.Terminal
        }
        _state.value = AuthState.Refreshing
        val outcome = runBlocking { performRefresh(refreshToken) }
        when (outcome) {
            is RefreshOutcome.Success -> _state.value = AuthState.Authenticated
            RefreshOutcome.Recoverable -> _state.value = AuthState.RecoverableRefreshFailed
            RefreshOutcome.Terminal -> handleTerminal()
        }
        outcome
    }

    private suspend fun performRefresh(refreshToken: String): RefreshOutcome = try {
        val dto = authBootstrapApi.refresh(tokenStore.miniAppId, tokenStore.deviceId, JwtRefreshRequest(refreshToken))
        val data = dto.data
        if (dto.success && data != null && data.accessToken.isNotBlank()) {
            tokenStore.commitTokens(data.accessToken, data.refreshToken)
            RefreshOutcome.Success(data.accessToken)
        } else {
            RefreshOutcome.Terminal // 200 envelope without a usable token
        }
    } catch (e: HttpException) {
        when (e.code()) {
            400, 401, 403, 422 -> RefreshOutcome.Terminal
            else -> RefreshOutcome.Recoverable // 408/429/5xx/other → keep session, back off
        }
    } catch (e: IOException) {
        RefreshOutcome.Recoverable
    } catch (e: Throwable) {
        RefreshOutcome.Recoverable
    }

    private fun handleTerminal() {
        tokenStore.clear()
        setProfile(null)
        _state.value = AuthState.AuthFailed
    }

    private fun setProfile(profile: Profile?) {
        _profile.value = profile
        _roleAccess.value = RoleAccess.from(profile)
    }
}
