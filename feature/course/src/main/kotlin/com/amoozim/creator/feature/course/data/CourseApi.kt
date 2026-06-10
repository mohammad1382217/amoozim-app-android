package com.amoozim.creator.feature.course.data

import com.amoozim.creator.core.model.BaseDto
import com.amoozim.creator.feature.course.model.Course
import com.amoozim.creator.feature.course.model.LessonItem
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CourseApi {

    @GET("mini-apps/{miniappId}/courses")
    suspend fun listCourses(
        @Path("miniappId") miniappId: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("filter[title]") title: String? = null,
    ): BaseDto<List<Course>>

    @GET("mini-apps/{miniappId}/courses/{courseId}")
    suspend fun getCourse(
        @Path("miniappId") miniappId: String,
        @Path("courseId") courseId: Int,
    ): BaseDto<Course>

    @GET("mini-apps/{miniappId}/courses/{courseId}/lessons")
    suspend fun listLessons(
        @Path("miniappId") miniappId: String,
        @Path("courseId") courseId: Int,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
    ): BaseDto<List<LessonItem>>
}
