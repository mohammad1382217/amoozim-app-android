package com.amoozim.creator.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Request body for `POST auth/jwt/callback` — exchanges an Eitaa initData blob for a JWT. */
@Serializable
data class JwtCallbackRequest(
    @SerialName("eitaa_data") val eitaaData: String,
)

/** Response of `POST auth/jwt/callback` (`profileStore.ts` `JwtCallbackResponse`). */
@Serializable
data class JwtCallbackResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("expires_in") val expiresIn: Long = 0,
    @SerialName("token_type") val tokenType: String = "Bearer",
    @SerialName("mini_app_uuid") val miniAppUuid: String = "",
    val user: Profile? = null,
)

/** Request body for `POST auth/jwt/refresh`. */
@Serializable
data class JwtRefreshRequest(
    @SerialName("refresh_token") val refreshToken: String,
)

/** Response of `POST auth/jwt/refresh` (`models/auth/JwtRefreshResponse.ts`). */
@Serializable
data class JwtRefreshResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("expires_in") val expiresIn: Long = 0,
    @SerialName("token_type") val tokenType: String = "Bearer",
    @SerialName("mini_app_uuid") val miniAppUuid: String = "",
)
