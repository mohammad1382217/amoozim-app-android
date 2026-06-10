package com.amoozim.creator.ui

import androidx.lifecycle.ViewModel
import com.amoozim.creator.core.session.AuthState
import com.amoozim.creator.core.session.RoleAccess
import com.amoozim.creator.core.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/** App-level state for routing decisions (auth lifecycle + role-gated navigation). */
@HiltViewModel
class AppViewModel @Inject constructor(
    session: SessionManager,
) : ViewModel() {

    val authState: StateFlow<AuthState> = session.state
    val roleAccess: StateFlow<RoleAccess> = session.roleAccess

    /** True when persisted tokens already establish a session at cold start. */
    val isInitiallyAuthenticated: Boolean = session.state.value == AuthState.Authenticated
}
