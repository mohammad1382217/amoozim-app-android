package com.amoozim.creator.core.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.ThreadLocalRandom
import javax.inject.Inject
import kotlin.math.min

/**
 * App-level retry with exponential backoff, mirroring the web client's policy:
 * only idempotent GETs are retried, on the status set {408,429,500,502,503,504} or
 * an [IOException]; up to [MAX_RETRIES] times; delay = min(60s, 1s * 2^attempt) + jitter.
 * Mutations are never auto-retried.
 */
class RetryInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val retryable = request.method == "GET"
        var attempt = 0
        var lastError: IOException? = null

        while (true) {
            try {
                val response = chain.proceed(request)
                if (retryable && response.code in RETRYABLE_CODES && attempt < MAX_RETRIES) {
                    response.close()
                    backoff(attempt)
                    attempt++
                    continue
                }
                return response
            } catch (e: IOException) {
                lastError = e
                if (!retryable || attempt >= MAX_RETRIES) throw e
                backoff(attempt)
                attempt++
            }
        }
    }

    private fun backoff(attempt: Int) {
        val base = min(MAX_DELAY_MS, INITIAL_DELAY_MS shl attempt)
        val jitter = ThreadLocalRandom.current().nextLong(0, JITTER_MS + 1)
        try {
            Thread.sleep(base + jitter)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            throw IOException("Retry backoff interrupted", e)
        }
    }

    private companion object {
        const val MAX_RETRIES = 3
        const val INITIAL_DELAY_MS = 1000L
        const val MAX_DELAY_MS = 60_000L
        const val JITTER_MS = 1000L
        val RETRYABLE_CODES = setOf(408, 429, 500, 502, 503, 504)
    }
}
