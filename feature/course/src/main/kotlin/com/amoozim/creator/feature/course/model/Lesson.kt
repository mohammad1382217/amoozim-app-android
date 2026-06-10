package com.amoozim.creator.feature.course.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A lesson (`src/features/lesson/model/Lesson.ts`). Media lives on the lesson's
 * content blocks, not the lesson itself. The list endpoint wraps each lesson in a
 * [LessonItem] with prev/next navigation hints.
 */
@Serializable
data class Lesson(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    @SerialName("is_free") val isFree: Boolean = false,
    val status: Int = 0,
    @SerialName("status_label") val statusLabel: String = "",
    @SerialName("cover_url") val coverUrl: String? = null,
    @SerialName("has_quiz") val hasQuiz: Boolean = false,
    @SerialName("is_visited") val isVisited: Boolean? = null,
    @SerialName("sort_order") val sortOrder: Int? = null,
    @SerialName("external_media") val externalMedia: LessonMediaToken? = null,
)

@Serializable
data class LessonMediaToken(
    @SerialName("media_token") val mediaToken: String = "",
)

@Serializable
data class LessonItem(
    val data: Lesson = Lesson(),
    @SerialName("previous_lessonId") val previousLessonId: Int? = null,
    @SerialName("next_lessonId") val nextLessonId: Int? = null,
    @SerialName("is_visited") val isVisited: Boolean? = null,
)
