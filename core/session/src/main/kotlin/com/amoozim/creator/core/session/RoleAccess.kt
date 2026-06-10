package com.amoozim.creator.core.session

import com.amoozim.creator.core.model.Profile

/**
 * Tri-state role resolution, mirroring the web `useRoleAccess` hook. The UI must
 * NOT render the regular-user layout while the role is still [Resolving] (e.g. the
 * bottom nav shows placeholders), so the unknown state is explicit.
 *
 * Role enum (`permissions.ts`): OWNER=1, ADMIN=2, USER=3; `is_super_admin` overrides
 * to privileged; unknown → least privilege (regular).
 */
sealed interface RoleAccess {
    data object Resolving : RoleAccess
    data object Privileged : RoleAccess
    data object Regular : RoleAccess

    val isPrivileged: Boolean get() = this is Privileged
    val isResolving: Boolean get() = this is Resolving

    companion object {
        fun from(profile: Profile?): RoleAccess = when {
            profile == null -> Resolving
            profile.isSuperAdmin == true -> Privileged
            profile.role == Profile.ROLE_OWNER || profile.role == Profile.ROLE_ADMIN -> Privileged
            else -> Regular
        }
    }
}
