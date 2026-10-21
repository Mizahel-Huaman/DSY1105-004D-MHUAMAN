package com.example.insertcode.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.insertcode.ui.screens.ListadoOfflineScreen
import com.example.insertcode.ui.screens.RegistroIntegralScreen
import com.example.insertcode.ui.screens.ResumenScreen
import com.example.insertcode.viewmodels.UsuarioViewModel
import com.example.insertcode.viewmodels.UsuarioViewModelFactory

@Composable
fun AppNavigation(factory: UsuarioViewModelFactory) {
    val navController = rememberNavController()
    // Un único ViewModel (ligado a la Activity) compartido entre todas las pantallas
    val usuarioViewModel: UsuarioViewModel = viewModel(factory = factory)

    NavHost(
        navController = navController,
        startDestination = "registro"
    ) {
        composable(route = "registro") {
            RegistroIntegralScreen(navController = navController, viewModel = usuarioViewModel)
        }
        composable(route = "resumen") {
            ResumenScreen(navController = navController, viewModel = usuarioViewModel)
        }
        composable(route = "listado") {
            ListadoOfflineScreen(navController = navController, viewModel = usuarioViewModel)
        }
    }
}
