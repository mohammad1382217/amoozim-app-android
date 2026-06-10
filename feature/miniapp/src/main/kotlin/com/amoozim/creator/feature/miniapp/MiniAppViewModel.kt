package com.amoozim.creator.feature.miniapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amoozim.creator.core.common.result.ApiResult
import com.amoozim.creator.core.session.SessionManager
import com.amoozim.creator.feature.miniapp.data.MiniAppRepository
import com.amoozim.creator.feature.miniapp.model.MiniApp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface MiniAppHeaderState {
    data object Loading : MiniAppHeaderState
    data class Ready(val miniApp: MiniApp) : MiniAppHeaderState
    data class Failed(val message: String) : MiniAppHeaderState
}

/** Loads the current mini-app's details for the header shown across the shell tabs. */
@HiltViewModel
class MiniAppViewModel @Inject constructor(
    private val repository: MiniAppRepository,
    session: SessionManager,
) : ViewModel() {

    private val miniappId: String = session.miniAppId

    private val _state = MutableStateFlow<MiniAppHeaderState>(MiniAppHeaderState.Loading)
    val state: StateFlow<MiniAppHeaderState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _state.value = MiniAppHeaderState.Loading
            _state.value = when (val result = repository.getDetails(miniappId)) {
                is ApiResult.Success -> MiniAppHeaderState.Ready(result.data)
                is ApiResult.Error -> MiniAppHeaderState.Failed(result.message)
            }
        }
    }
}
