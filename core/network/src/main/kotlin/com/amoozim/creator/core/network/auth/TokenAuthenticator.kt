package com.amoozim.creator.core.network.auth

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

/**
 * OkHttp [Authenticator] that reacts to a 401 by performing a single-flight token
 * refresh (delegated to [TokenRefresher], implemented by the session layer). On
 * success the original request is retried once with the new token; otherwise the
 * 401 is allowed to propagate. OkHttp serializes authenticator calls per connection,
 * and [responseCount] caps retries to avoid loops.
 */
class TokenAuthenticator @Inject constructor(
    private val refresher: TokenRefresher,
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= MAX_ATTEMPTS) return null

        val staleToken = response.request.header(HEADER)
            ?.removePrefix(BEARER_PREFIX)
            ?.trim()

        return when (val outcome = refresher.refreshBlocking(staleToken)) {
            is RefreshOutcome.Success ->
                response.request.newBuilder()
                    .header(HEADER, "$BEARER_PREFIX${outcome.accessToken}")
                    .build()

            RefreshOutcome.Recoverable, RefreshOutcome.Terminal -> null
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }

    private companion object {
        const val MAX_ATTEMPTS = 2
        const val HEADER = "Authorization"
        const val BEARER_PREFIX = "Bearer "
    }
}
