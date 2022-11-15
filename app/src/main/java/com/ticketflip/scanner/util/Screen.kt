package com.hva.amsix.util

/**
 * Screen metadata for Main section.
 */
sealed class Screen(val route: String) {

    // auth screens
    object AccessScreen : Screen("main")

    // top level screens
    object EventScreen : Screen("events")
    object ProfileScreen : Screen("profile")

    // scanning screens
    object ScanScreen : Screen("scan-screen")
    object ScanAccessScreen : Screen("scan-access-screen")


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
