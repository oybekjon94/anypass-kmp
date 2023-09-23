package io.spherelabs.anypass.ui.home.navigation

import io.spherelabs.anypass.navigation.Route
import io.spherelabs.anypass.ui.home.HomeRoute
import io.spherelabs.navigation.NavHostScope
import io.spherelabs.navigation.NavigationController
import io.spherelabs.navigation.composable

fun NavHostScope<Route>.homeScreen(
    navigateToCreatePassword: () -> Unit
) {
    this.composable<Route.Home> {
        HomeRoute {
            navigateToCreatePassword.invoke()
        }
    }
}

fun NavigationController<Route>.navigateToHome() {
    this.navigateTo(Route.Home)
}