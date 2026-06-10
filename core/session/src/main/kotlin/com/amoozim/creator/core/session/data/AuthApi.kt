package com.amoozim.creator.core.session.data

import com.amoozim.creator.core.model.BaseDto
import com.amoozim.creator.core.model.JwtCallbackRequest
import com.amoozim.creator.core.model.JwtCallbackResponse
import com.amoozim.creator.core.model.JwtRefreshRequest
import com.amoozim.creator.core.model.JwtRefreshResponse
import com.amoozim.creator.core.model.Profile
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Unauthenticated auth-bootstrap endpoints. Served on the PLAIN Retrofit (no bearer,
 * no 401 authenticator) to avoid self-deadlock — exactly the web client's
 * `auth: false, bypassAuthGate: true` semantics.
 */
interface AuthBootstrapApi {

    @POST("auth/jwt/callback")
    suspend fun callback(
        @Header("Miniapp-UUID") miniAppUuid: String,
        @Header("X-Device-Id") deviceId: String?,
        @Body body: JwtCallbackRequest,
    ): BaseDto<JwtCallbackResponse>

    @POST("auth/jwt/refresh")
    suspend fun refresh(
        @Header("Miniapp-UUID") miniAppUuid: String,
        @Header("X-Device-Id") deviceId: String?,
        @Body body: JwtRefreshRequest,
    ): BaseDto<JwtRefreshResponse>
}

/** Authenticated current-user endpoint. Served on the DEFAULT (authed) Retrofit. */
interface ProfileApi {

    @GET("auth/me")
    suspend fun me(): BaseDto<Profile>
}
