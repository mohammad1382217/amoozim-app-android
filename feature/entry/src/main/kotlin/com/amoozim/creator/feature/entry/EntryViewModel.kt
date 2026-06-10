package com.amoozim.creator.feature.entry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amoozim.creator.core.common.result.ApiResult
import com.amoozim.creator.core.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EntryUiState(
    val miniAppId: String = "",
    val initData: String = "",
    val deviceId: String = "",
    val accessToken: String = "",
    val refreshToken: String = "",
    /** When true, seed tokens directly (dev) instead of exchanging initData. */
    val useDevTokens: Boolean = false,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val authenticated: Boolean = false,
)

/**
 * Drives the session-bootstrap screen. The faithful path exchanges an Eitaa
 * `initData` blob for a JWT via [SessionManager.bootstrapWithInitData]; a dev path
 * seeds an existing token pair for UI testing when no Eitaa host is present.
 */
@HiltViewModel
class EntryViewModel @Inject constructor(
    private val session: SessionManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EntryUiState())
    val uiState: StateFlow<EntryUiState> = _uiState.asStateFlow()

    fun onMiniAppIdChange(value: String) = _uiState.update { it.copy(miniAppId = value, errorMessage = null) }
    fun onInitDataChange(value: String) = _uiState.update { it.copy(initData = value, errorMessage = null) }
    fun onDeviceIdChange(value: String) = _uiState.update { it.copy(deviceId = value) }
    fun onAccessTokenChange(value: String) = _uiState.update { it.copy(accessToken = value, errorMessage = null) }
    fun onRefreshTokenChange(value: String) = _uiState.update { it.copy(refreshToken = value, errorMessage = null) }
    fun setUseDevTokens(useDevTokens: Boolean) = _uiState.update { it.copy(useDevTokens = useDevTokens, errorMessage = null) }

    fun submit() {
        val state = _uiState.value
        if (state.isSubmitting) return

        if (state.miniAppId.isBlank()) {
            _uiState.update { it.copy(errorMessage = "شناسه برنامک (Miniapp UUID) الزامی است") }
            return
        }

        if (state.useDevTokens) {
            if (state.accessToken.isBlank() || state.refreshToken.isBlank()) {
                _uiState.update { it.copy(errorMessage = "توکن دسترسی و توکن تازه‌سازی الزامی است") }
                return
            }
            session.bootstrapWithTokens(
                miniAppId = state.miniAppId,
                accessToken = state.accessToken,
                refreshToken = state.refreshToken,
                deviceId = state.deviceId.ifBlank { null },
            )
            _uiState.update { it.copy(authenticated = true) }
            return
        }

        if (state.initData.isBlank()) {
            _uiState.update { it.copy(errorMessage = "مقدار initData الزامی است") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            when (val result = session.bootstrapWithInitData(state.miniAppId, state.initData, state.deviceId.ifBlank { null })) {
                is ApiResult.Success -> _uiState.update { it.copy(isSubmitting = false, authenticated = true) }
                is ApiResult.Error -> _uiState.update { it.copy(isSubmitting = false, errorMessage = result.message) }
            }
        }
    }
}
