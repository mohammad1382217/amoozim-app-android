package com.amoozim.creator.feature.course

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amoozim.creator.core.common.result.ApiResult
import com.amoozim.creator.core.session.SessionManager
import com.amoozim.creator.feature.course.data.CourseRepository
import com.amoozim.creator.feature.course.model.Course
import com.amoozim.creator.feature.course.model.Lesson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CourseDetailUiState(
    val isLoading: Boolean = true,
    val course: Course? = null,
    val lessons: List<Lesson> = emptyList(),
    val canAccessContent: Boolean = false,
    val errorMessage: String? = null,
)

/**
 * Loads a course and its lessons. `canAccessContent` mirrors the web `course-access`
 * rule: purchased OR privileged. The lesson list is rendered in server order.
 */
@HiltViewModel
class CourseDetailViewModel @Inject constructor(
    private val repository: CourseRepository,
    private val session: SessionManager,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val miniappId: String = session.miniAppId
    private val courseId: Int = savedStateHandle.get<Int>(ARG_COURSE_ID) ?: 0

    private val _uiState = MutableStateFlow(CourseDetailUiState())
    val uiState: StateFlow<CourseDetailUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val courseResult = repository.getCourse(miniappId, courseId)) {
                is ApiResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = courseResult.message) }
                    return@launch
                }
                is ApiResult.Success -> {
                    val course = courseResult.data
                    val lessons = when (val lessonsResult = repository.listLessons(miniappId, courseId)) {
                        is ApiResult.Success -> lessonsResult.data.items
                        is ApiResult.Error -> emptyList()
                    }
                    val privileged = session.roleAccess.value.isPrivileged
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            course = course,
                            lessons = lessons,
                            canAccessContent = course.isPurchased || privileged,
                        )
                    }
                }
            }
        }
    }

    companion object {
        const val ARG_COURSE_ID = "courseId"
    }
}
