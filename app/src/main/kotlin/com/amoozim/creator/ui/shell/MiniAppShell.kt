package com.amoozim.creator.ui.shell

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.amoozim.creator.core.session.RoleAccess
import com.amoozim.creator.feature.course.ui.CoursesScreen
import com.amoozim.creator.feature.miniapp.ui.WalletStatsScreen
import com.amoozim.creator.feature.profile.ProfileScreen
import com.amoozim.creator.ui.navigation.TabRoutes

/**
 * The authenticated mini-app shell: a [Scaffold] with a role-gated bottom navigation
 * bar hosting the tab graph. Course detail is opened on the OUTER nav graph (above the
 * bar), via [onCourseClick].
 */
@Composable
fun MiniAppShell(
    roleAccess: RoleAccess,
    onCourseClick: (Int) -> Unit,
    onSignedOut: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tabNavController = rememberNavController()
    val items = bottomNavItemsFor(roleAccess)

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by tabNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { item ->
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            tabNavController.navigate(item.route) {
                                popUpTo(tabNavController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = tabNavController,
            startDestination = TabRoutes.HOME,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(TabRoutes.HOME) { CoursesScreen(onCourseClick = onCourseClick) }
            composable(TabRoutes.USERS) { PlaceholderScreen(title = "کاربران") }
            composable(TabRoutes.PUBLISH) { PlaceholderScreen(title = "انتشار") }
            composable(TabRoutes.WALLET) { WalletStatsScreen() }
            composable(TabRoutes.MY_COURSES) { PlaceholderScreen(title = "آموزش‌های من") }
            composable(TabRoutes.PROFILE) { ProfileScreen(onSignedOut = onSignedOut) }
        }
    }
}
