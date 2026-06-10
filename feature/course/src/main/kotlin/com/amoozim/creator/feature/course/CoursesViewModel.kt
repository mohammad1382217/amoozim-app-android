package com.amoozim.creator.feature.course

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amoozim.creator.core.common.result.ApiResult
import com.amoozim.creator.core.session.SessionManager
import com.amoozim.creator.feature.course.data.CourseRepository
import com.amoozim.creator.feature.course.model.Course
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CoursesUiState(
    val query: String = "",
    val courses: List<Course> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val errorMessage: String? = null,
    val endReached: Boolean = false,
)

/**
 * Backs the home tab's course list: a debounced title search plus offset pagination
 * (mirrors the web `CourseList` infinite scroll, per_page 12).
 */
@HiltViewModel
class CoursesViewModel @Inject constructor(
    private val repository: CourseRepository,
    session: SessionManager,
) : ViewModel() {

    private val miniappId: String = session.miniAppId

    private val _uiState = MutableStateFlow(CoursesUiState())
    val uiState: StateFlow<CoursesUiState> = _uiState.asStateFlow()

    private var currentPage = 0
    private var searchJob: Job? = null

    init {
        loadFirstPage()
    }

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MS)
            loadFirstPage()
        }
    }

    fun loadFirstPage() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = repository.listCourses(miniappId, page = 1, title = _uiState.value.query)) {
                is ApiResult.Success -> {
                    currentPage = result.data.page
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            courses = result.data.items,
                            endReached = !result.data.hasNextPage,
                        )
                    }
                }
                is ApiResult.Error -> _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
            }
        }
    }

    fun loadNextPage() {
        val state = _uiState.value
        if (state.isLoading || state.isLoadingMore || state.endReached) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }
            when (val result = repository.listCourses(miniappId, page = currentPage + 1, title = state.query)) {
                is ApiResult.Success -> {
                    currentPage = result.data.page
                    _uiState.update {
                        it.copy(
                            isLoadingMore = false,
                            courses = it.courses + result.data.items,
                            endReached = !result.data.hasNextPage,
                        )
                    }
                }
                is ApiResult.Error -> _uiState.update { it.copy(isLoadingMore = false, errorMessage = result.message) }
            }
        }
    }

    private companion object {
        const val SEARCH_DEBOUNCE_MS = 500L
    }
}
