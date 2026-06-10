package com.amoozim.creator.feature.course.data

import com.amoozim.creator.core.common.result.ApiResult
import com.amoozim.creator.core.common.result.Paged
import com.amoozim.creator.core.common.result.map
import com.amoozim.creator.core.network.NetworkCaller
import com.amoozim.creator.feature.course.model.Course
import com.amoozim.creator.feature.course.model.Lesson
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CourseRepository @Inject constructor(
    private val api: CourseApi,
    private val caller: NetworkCaller,
) {
    suspend fun listCourses(
        miniappId: String,
        page: Int,
        perPage: Int = DEFAULT_COURSES_PER_PAGE,
        title: String? = null,
    ): ApiResult<Paged<Course>> =
        caller.callPaged { api.listCourses(miniappId, page, perPage, title?.ifBlank { null }) }

    suspend fun getCourse(miniappId: String, courseId: Int): ApiResult<Course> =
        caller.call { api.getCourse(miniappId, courseId) }

    /** Returns the lessons unwrapped from their [com.amoozim.creator.feature.course.model.LessonItem] envelope. */
    suspend fun listLessons(
        miniappId: String,
        courseId: Int,
        page: Int = 1,
        perPage: Int = DEFAULT_LESSONS_PER_PAGE,
    ): ApiResult<Paged<Lesson>> =
        caller.callPaged { api.listLessons(miniappId, courseId, page, perPage) }
            .map { paged ->
                Paged(
                    items = paged.items.map { it.data },
                    page = paged.page,
                    perPage = paged.perPage,
                    total = paged.total,
                    lastPage = paged.lastPage,
                )
            }

    private companion object {
        const val DEFAULT_COURSES_PER_PAGE = 12
        const val DEFAULT_LESSONS_PER_PAGE = 50
    }
}
