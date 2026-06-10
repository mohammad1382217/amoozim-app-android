package com.amoozim.creator.feature.miniapp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A creator's mini-app. The web app has two slightly divergent `MiniApp` interfaces;
 * this is their reconciled superset (see the port spec). Returned by
 * `GET mini-apps/{id}/details`.
 */
@Serializable
data class MiniApp(
    val id: Int? = null,
    val title: String = "",
    @SerialName("miniapp_eitaa_username") val miniAppEitaaUsername: String? = null,
    @SerialName("miniapp_eitaa_id") val miniAppEitaaId: String? = null,
    @SerialName("channel_username") val channelUsername: String? = null,
    @SerialName("channel_support_id") val channelSupportId: String? = null,
    @SerialName("channel_id") val channelId: String? = null,
    @SerialName("start_message") val startMessage: String? = null,
    @SerialName("start_image") val startImage: String? = null,
    val bio: String? = null,
    val cover: String? = null,
    @SerialName("cover_thumb") val coverThumb: String? = null,
    @SerialName("phone_number") val phoneNumber: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("referral_code") val referralCode: String = "",
    @SerialName("referred_by_uuid") val referredByUuid: String? = null,
) {
    /** Best available avatar/cover image reference for display. */
    val displayImage: String? get() = cover?.takeIf { it.isNotBlank() } ?: coverThumb?.takeIf { it.isNotBlank() }
}
