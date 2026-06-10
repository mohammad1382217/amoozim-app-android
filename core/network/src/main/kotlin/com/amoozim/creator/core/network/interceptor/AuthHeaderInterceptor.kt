package com.amoozim.creator.core.network.interceptor

import com.amoozim.creator.core.network.auth.TokenProvider
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * Adds `Authorization: Bearer <token>` when an access token exists and the request
 * doesn't already carry one. Mirrors the web client, which attaches the bearer only
 * when a token is present (public endpoints simply send no auth).
 */
class AuthHeaderInterceptor @Inject constructor(
    private val tokenProvider: TokenProvider,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val token = tokenProvider.accessToken()
        val request = if (!token.isNullOrBlank() && original.header(HEADER) == null) {
            original.newBuilder().header(HEADER, "Bearer $token").build()
        } else {
            original
        }
        return chain.proceed(request)
    }

    private companion object {
        const val HEADER = "Authorization"
    }
}
