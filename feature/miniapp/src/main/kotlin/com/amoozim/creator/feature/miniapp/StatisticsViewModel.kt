package com.amoozim.creator.feature.miniapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amoozim.creator.core.common.result.ApiResult
import com.amoozim.creator.core.session.SessionManager
import com.amoozim.creator.feature.miniapp.data.MiniAppRepository
import com.amoozim.creator.feature.miniapp.model.MiniappStatistics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StatisticsUiState(
    val isLoading: Boolean = true,
    val statistics: MiniappStatistics? = null,
    val errorMessage: String? = null,
)

/** Loads the mini-app statistics shown on the wallet tab (`GET mini-apps/{id}/statistics`). */
@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val repository: MiniAppRepository,
    session: SessionManager,
) : ViewModel() {

    private val miniappId: String = session.miniAppId

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            _uiState.update {
                when (val result = repository.getStatistics(miniappId)) {
                    is ApiResult.Success -> it.copy(isLoading = false, statistics = result.data)
                    is ApiResult.Error -> it.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }
}
