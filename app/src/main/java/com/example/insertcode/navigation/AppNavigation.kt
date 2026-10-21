package com.example.insertcode.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.insertcode.ui.screens.HomeAdminScreen
import com.example.insertcode.ui.screens.HomeOperadorScreen
import com.example.insertcode.ui.screens.HomeSupervisorScreen
import com.example.insertcode.ui.screens.LoginScreen
import com.example.insertcode.viewmodels.LoginViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val loginViewModel: LoginViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(route = Screen.Login.route) {
            LoginScreen(
                loginViewModel = loginViewModel,
                onLoginExitoso = { destino ->
                    navController.navigate(destino.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(route = Screen.HomeAdmin.route) {
            HomeAdminScreen(
                loginViewModel = loginViewModel,
                onLogout = { navController.navigate(Screen.Login.route) }
            )
        }
        composable(route = Screen.HomeSupervisor.route) {
            HomeSupervisorScreen(
                loginViewModel = loginViewModel,
                onLogout = { navController.navigate(Screen.Login.route) }
            )
        }
        composable(route = Screen.HomeOperador.route) {
            HomeOperadorScreen(
                loginViewModel = loginViewModel,
                onLogout = { navController.navigate(Screen.Login.route) }
            )
        }
    }
}