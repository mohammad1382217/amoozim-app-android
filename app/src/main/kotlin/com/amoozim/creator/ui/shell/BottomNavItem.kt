package com.amoozim.creator.ui.shell

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Upload
import androidx.compose.ui.graphics.vector.ImageVector
import com.amoozim.creator.core.session.RoleAccess
import com.amoozim.creator.ui.navigation.TabRoutes

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
)

private val Home = BottomNavItem(TabRoutes.HOME, "خانه", Icons.Filled.Home)
private val Users = BottomNavItem(TabRoutes.USERS, "کاربران", Icons.Filled.Group)
private val Publish = BottomNavItem(TabRoutes.PUBLISH, "انتشار", Icons.Filled.Upload)
private val Wallet = BottomNavItem(TabRoutes.WALLET, "کیف پول", Icons.Filled.AccountBalanceWallet)
private val MyCourses = BottomNavItem(TabRoutes.MY_COURSES, "آموزش‌های من", Icons.Filled.School)
private val Profile = BottomNavItem(TabRoutes.PROFILE, "پروفایل", Icons.Filled.Person)

/**
 * Role-gated tab set, mirroring the web bottom nav: privileged creators get the full
 * management set; regular users get home + my-courses; while the role is still
 * resolving, show only the always-present tabs (never the regular layout prematurely).
 */
fun bottomNavItemsFor(roleAccess: RoleAccess): List<BottomNavItem> = when (roleAccess) {
    RoleAccess.Privileged -> listOf(Home, Users, Publish, Wallet, Profile)
    RoleAccess.Regular -> listOf(Home, MyCourses, Profile)
    RoleAccess.Resolving -> listOf(Home, Profile)
}
