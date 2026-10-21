package com.example.insertcode.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.insertcode.ui.screens.RegistroScreen
import com.example.insertcode.ui.screens.ResumenScreen
import com.example.insertcode.viewmodels.UsuarioViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // Instanciamos el ViewModel aquí para compartirlo entre pantallas
    val usuarioViewModel: UsuarioViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "registro"
    ) {
        composable(route = "registro") {
            RegistroScreen(navController = navController, viewModel = usuarioViewModel)
        }
        composable(route = "resumen") {
            ResumenScreen(viewModel = usuarioViewModel)
        }
    }
}