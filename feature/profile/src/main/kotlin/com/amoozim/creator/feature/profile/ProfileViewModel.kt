package com.amoozim.creator.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amoozim.creator.core.common.result.ApiResult
import com.amoozim.creator.core.model.Profile
import com.amoozim.creator.core.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val isLoading: Boolean = false,
    val profile: Profile? = null,
    val errorMessage: String? = null,
)

/**
 * Surfaces the current user from [SessionManager]. Hydrates from `auth/me` when the
 * profile is missing (e.g. warm start with only persisted tokens).
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val session: SessionManager,
) : ViewModel() {

    private val isLoading = MutableStateFlow(false)
    private val error = MutableStateFlow<String?>(null)

    val uiState: StateFlow<ProfileUiState> =
        combine(session.profile, isLoading, error) { profile, loading, errorMessage ->
            ProfileUiState(isLoading = loading, profile = profile, errorMessage = errorMessage)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
            initialValue = ProfileUiState(profile = session.profile.value),
        )

    init {
        if (session.profile.value == null) refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            isLoading.value = true
            error.value = null
            val result = session.refreshProfile()
            if (result is ApiResult.Error) error.value = result.message
            isLoading.value = false
        }
    }

    fun signOut() = session.signOutLocally()

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}
