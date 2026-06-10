package com.amoozim.creator.feature.course.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A course within a mini-app (`src/features/course/model/Course.ts`). Prices are flat
 * integers (Toman); there is no nested price object. `published_status` is an enum:
 * 0 = پیش‌نویس, 1 = در انتظار بررسی, 2 = منتشر شده.
 */
@Serializable
data class Course(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val price: Long = 0,
    @SerialName("special_price") val specialPrice: Long? = null,
    @SerialName("final_price") val finalPrice: Long = 0,
    @SerialName("published_status") val publishedStatus: Int = 0,
    @SerialName("published_label") val publishedLabel: String = "",
    @SerialName("cover_url") val coverUrl: String? = null,
    @SerialName("video_preview_url") val videoPreviewUrl: String? = null,
    @SerialName("has_certificate") val hasCertificate: Boolean = false,
    @SerialName("sort_order") val sortOrder: Int? = null,
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("updated_at") val updatedAt: String = "",
    @SerialName("is_purchased") val isPurchased: Boolean = false,
    @SerialName("lessons_count") val lessonsCount: Int? = null,
    @SerialName("users_count") val usersCount: Int = 0,
    @SerialName("progress_percentage") val progressPercentage: Int = 0,
    val categories: List<CourseCategory>? = null,
    @SerialName("external_media") val externalMedia: ExternalMedia? = null,
    val settings: CourseSettings? = null,
    @SerialName("support_id") val supportId: String? = null,
) {
    companion object {
        const val STATUS_DRAFT = 0
        const val STATUS_PENDING_REVIEW = 1
        const val STATUS_PUBLISHED = 2
    }
}

@Serializable
data class CourseCategory(
    val id: Int = 0,
    val name: String = "",
)

@Serializable
data class ExternalMedia(
    @SerialName("media_type") val mediaType: String = "",
    @SerialName("media_token") val mediaToken: String = "",
)

@Serializable
data class CourseSettings(
    @SerialName("visibility_status") val visibilityStatus: Int = 0,
    @SerialName("availability_status") val availabilityStatus: Int = 0,
    @SerialName("content_protection") val contentProtection: Boolean = false,
    @SerialName("show_participants_count") val showParticipantsCount: Boolean = false,
    @SerialName("requires_complete_profile") val requiresCompleteProfile: Boolean = false,
    @SerialName("show_in_vitrin") val showInVitrin: Boolean = false,
    @SerialName("limit_devices") val limitDevices: Boolean = false,
    @SerialName("max_devices") val maxDevices: Int = 0,
)
