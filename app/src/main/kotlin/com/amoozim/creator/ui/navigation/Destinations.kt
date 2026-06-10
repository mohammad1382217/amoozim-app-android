package com.amoozim.creator.ui.navigation

/** Top-level navigation destinations (outer NavHost). */
object Routes {
    const val ENTRY = "entry"
    const val SHELL = "shell"

    const val ARG_COURSE_ID = "courseId"
    const val COURSE_DETAIL = "course/{$ARG_COURSE_ID}"

    fun courseDetail(courseId: Int): String = "course/$courseId"
}

/** Tab destinations inside the mini-app shell (nested NavHost). */
object TabRoutes {
    const val HOME = "home"
    const val USERS = "users"
    const val PUBLISH = "publish"
    const val WALLET = "wallet"
    const val MY_COURSES = "my_courses"
    const val PROFILE = "profile"
}
