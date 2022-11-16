package com.hva.amsix.util

/**
 * Screen metadata for Main section.
 */
sealed class Screen(val route: String) {

    // auth screens
    object AccessScreen : Screen("access")
    object AccessScanScreen : Screen("access-scan")

    // event screens
    object EventScreen : Screen("event")
    object EventScanScreen : Screen("event-scan")

    // Profile
    object ProfileScreen : Screen("profile")


    // append arguments to route
    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}
