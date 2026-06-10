package com.amoozim.creator.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The authenticated user, returned by `auth/jwt/callback` (as `user`) and `auth/me`.
 * Ported from `profileStore.ts` `Profile`. Optional/nullable fields carry defaults so
 * deserialization is resilient to partial payloads.
 */
@Serializable
data class Profile(
    val id: Int = 0,
    @SerialName("eitaa_id") val eitaaId: String = "",
    val username: String = "",
    val name: String = "",
    @SerialName("first_name") val firstName: String = "",
    @SerialName("last_name") val lastName: String = "",
    @SerialName("display_name") val displayName: String = "",
    val phone: String? = null,
    @SerialName("is_super_admin") val isSuperAdmin: Boolean? = null,
    @SerialName("last_seen") val lastSeen: String? = null,
    @SerialName("progress_percentage") val progressPercentage: Int? = null,
    /** OWNER=1, ADMIN=2, USER=3 (see RoleAccess). */
    val role: Int = ROLE_USER,
    val avatar: String = "",
    val birthdate: String? = null,
    val gender: Int? = null,
    @SerialName("nationality_code") val nationalityCode: String? = null,
    @SerialName("profile_completed") val profileCompleted: Boolean = false,
    @SerialName("isParticipant") val isParticipant: Boolean = false,
    @SerialName("isJoinRequired") val isJoinRequired: Boolean = false,
) {
    companion object {
        const val ROLE_OWNER = 1
        const val ROLE_ADMIN = 2
        const val ROLE_USER = 3
    }
}
