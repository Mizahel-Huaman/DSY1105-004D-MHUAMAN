package com.example.insertcode.navigation

/**
 * Sealed class con las rutas oficiales requeridas por la evaluación.
 */
sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object HomeAdmin : Screen("home_admin")
    data object HomeSupervisor : Screen("home_supervisor")
    data object HomeOperador : Screen("home_operador")
}