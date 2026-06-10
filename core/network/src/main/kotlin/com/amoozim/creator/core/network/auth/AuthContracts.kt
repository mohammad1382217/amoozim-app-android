package com.amoozim.creator.core.network.auth

/**
 * Dependency-inversion seam between the network layer and the session layer.
 *
 * The network layer owns the OkHttp interceptor + authenticator that attach the
 * bearer token and refresh it on 401, but it must NOT depend on the session module
 * (which owns token persistence and the refresh HTTP call). So the network layer
 * declares these contracts and the session layer provides the implementations.
 */
interface TokenProvider {
    /** Current access token, or null when unauthenticated. Read synchronously on the OkHttp thread. */
    fun accessToken(): String?

    /** Current refresh token, or null. */
    fun refreshToken(): String?
}

/**
 * Performs a single-flight token refresh. Called by the OkHttp [okhttp3.Authenticator]
 * on the I/O thread, so the implementation must block until the refresh resolves.
 */
interface TokenRefresher {
    /**
     * @param staleAccessToken the token that was on the failed request, used to detect
     *   that another thread already refreshed (avoiding a redundant network call).
     */
    fun refreshBlocking(staleAccessToken: String?): RefreshOutcome
}

/**
 * Outcome of a refresh attempt, mirroring the web client's three-way classification
 * (`refreshAccessToken` in `fetchInstanceNew.ts`).
 */
sealed interface RefreshOutcome {
    /** New access token committed; retry the original request with it. */
    data class Success(val accessToken: String) : RefreshOutcome

    /** Transient failure (5xx/timeout/network). Keep tokens, do NOT retry or log out. */
    data object Recoverable : RefreshOutcome

    /** Refresh rejected (401/403/400/422/no-token). Tokens cleared; session ended. */
    data object Terminal : RefreshOutcome
}
