package com.amoozim.creator.core.session.data

import android.content.Context
import com.amoozim.creator.core.network.auth.TokenProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Persists the JWT pair and the mini-app context, and serves them synchronously to
 * the OkHttp interceptors via [TokenProvider]. Backed by [android.content.SharedPreferences]
 * because the interceptor/authenticator read tokens on the network thread without
 * suspending; an in-memory cache is the read source of truth.
 *
 * NOTE: SharedPreferences is not encrypted. For production, back this with
 * `EncryptedSharedPreferences` (androidx.security-crypto) — the public API here does
 * not change.
 */
@Singleton
class TokenStore @Inject constructor(
    @ApplicationContext context: Context,
) : TokenProvider {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    @Volatile private var cachedAccess: String? = prefs.getString(KEY_ACCESS, null)
    @Volatile private var cachedRefresh: String? = prefs.getString(KEY_REFRESH, null)

    @Volatile
    var miniAppId: String = prefs.getString(KEY_MINIAPP, "").orEmpty()
        private set

    @Volatile
    var deviceId: String? = prefs.getString(KEY_DEVICE, null)
        private set

    override fun accessToken(): String? = cachedAccess

    override fun refreshToken(): String? = cachedRefresh

    fun hasSession(): Boolean = !cachedAccess.isNullOrBlank()

    @Synchronized
    fun commitTokens(access: String, refresh: String) {
        cachedAccess = access
        cachedRefresh = refresh
        prefs.edit().putString(KEY_ACCESS, access).putString(KEY_REFRESH, refresh).apply()
    }

    @Synchronized
    fun setContext(miniAppId: String, deviceId: String?) {
        this.miniAppId = miniAppId
        this.deviceId = deviceId
        prefs.edit().putString(KEY_MINIAPP, miniAppId).putString(KEY_DEVICE, deviceId).apply()
    }

    @Synchronized
    fun clear() {
        cachedAccess = null
        cachedRefresh = null
        prefs.edit().remove(KEY_ACCESS).remove(KEY_REFRESH).apply()
    }

    private companion object {
        const val PREFS_NAME = "amoozim_session"
        const val KEY_ACCESS = "access_token"
        const val KEY_REFRESH = "refresh_token"
        const val KEY_MINIAPP = "miniapp_uuid"
        const val KEY_DEVICE = "device_id"
    }
}
