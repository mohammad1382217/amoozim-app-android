package com.amoozim.creator.core.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.util.UUID
import javax.inject.Inject

/** Attaches a unique `X-Request-Id` to every request, matching the web client. */
class RequestIdInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .header("X-Request-Id", UUID.randomUUID().toString())
            .build()
        return chain.proceed(request)
    }
}
