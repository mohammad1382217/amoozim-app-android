package com.amoozim.creator.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.amoozim.creator.core.session.AuthState
import com.amoozim.creator.feature.course.ui.CourseDetailScreen
import com.amoozim.creator.feature.entry.EntryScreen
import com.amoozim.creator.ui.navigation.Routes
import com.amoozim.creator.ui.shell.MiniAppShell

/**
 * Root composable. Owns the outer navigation graph (entry → shell → course detail)
 * and reacts to a terminal auth failure by returning to the entry screen.
 */
@Composable
fun AmoozimApp() {
    val appViewModel: AppViewModel = hiltViewModel()
    val authState by appViewModel.authState.collectAsStateWithLifecycle()
    val roleAccess by appViewModel.roleAccess.collectAsStateWithLifecycle()

    val navController = rememberNavController()
    val startDestination = remember {
        if (appViewModel.isInitiallyAuthenticated) Routes.SHELL else Routes.ENTRY
    }

    // A refresh that fails terminally clears the session — bounce back to entry.
    LaunchedEffect(authState) {
        if (authState == AuthState.AuthFailed) {
            navController.navigate(Routes.ENTRY) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.ENTRY) {
            EntryScreen(
                onAuthenticated = {
                    navController.navigate(Routes.SHELL) {
                        popUpTo(Routes.ENTRY) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.SHELL) {
            MiniAppShell(
                roleAccess = roleAccess,
                onCourseClick = { courseId -> navController.navigate(Routes.courseDetail(courseId)) },
                onSignedOut = {
                    navController.navigate(Routes.ENTRY) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }

        composable(
            route = Routes.COURSE_DETAIL,
            arguments = listOf(navArgument(Routes.ARG_COURSE_ID) { type = NavType.IntType }),
        ) {
            CourseDetailScreen(onBack = { navController.popBackStack() })
        }
    }
}
