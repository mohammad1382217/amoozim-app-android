package com.amoozim.creator.core.session

/**
 * Lifecycle of the authenticated session, mirroring the web auth state machine
 * (`auth-state-machine.ts`). The UI observes this to choose the start destination
 * and to react to a terminal failure (return to entry + "session expired").
 */
enum class AuthState {
    /** No session yet; bootstrap not started. */
    Idle,

    /** Exchanging initData for a JWT. */
    Authenticating,

    /** Valid access token in hand. */
    Authenticated,

    /** A 401 triggered a refresh that is in flight. */
    Refreshing,

    /** Refresh failed transiently; tokens kept, session still considered usable. */
    RecoverableRefreshFailed,

    /** Never authenticated / bootstrap failed. */
    Unauthenticated,

    /** Refresh rejected terminally; tokens cleared. */
    AuthFailed,
    ;

    /** Whether protected requests may proceed (gate "pass" states on the web). */
    val isReady: Boolean get() = this == Authenticated || this == RecoverableRefreshFailed

    /** Whether the user must (re)bootstrap a session. */
    val needsBootstrap: Boolean get() = this == Idle || this == Unauthenticated || this == AuthFailed
}
