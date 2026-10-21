package com.example.insertcode

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.insertcode.navigation.NavigationEvent
import com.example.insertcode.navigation.Screen
import com.example.insertcode.ui.screens.HomeAdminScreen
import com.example.insertcode.ui.screens.HomeOperadorScreen
import com.example.insertcode.ui.screens.HomeSupervisorScreen
import com.example.insertcode.ui.screens.LoginScreen
import com.example.insertcode.ui.theme.InsertcodeTheme
import com.example.insertcode.viewmodels.LoginViewModel
import com.example.insertcode.viewmodels.MainViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * Activity principal que inicializa el NavHost, comparte el LoginViewModel entre pantallas
 * y escucha los eventos de navegación con LaunchedEffect (Guías 10 y 11).
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InsertcodeTheme {
                val navController = rememberNavController()
                // Instanciamos ambos ViewModels en el alcance de la Activity para compartirlos
                val mainViewModel: MainViewModel = viewModel()
                val loginViewModel: LoginViewModel = viewModel()

                // Colecta reactiva de eventos de navegación desde MainViewModel (Guía 10)
                LaunchedEffect(Unit) {
                    mainViewModel.navigationEvents.collectLatest { event ->
                        when (event) {
                            is NavigationEvent.NavigateTo -> {
                                navController.navigate(event.route.route) {
                                    event.popUpToRoute?.let { popUp ->
                                        popUpTo(popUp.route) {
                                            inclusive = event.inclusive
                                        }
                                    }
                                    launchSingleTop = event.singleTop
                                }
                            }
                            is NavigationEvent.PopBackStack -> navController.popBackStack()
                            is NavigationEvent.NavigateUp -> navController.navigateUp()
                        }
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Login.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(route = Screen.Login.route) {
                            LoginScreen(
                                loginViewModel = loginViewModel,
                                onLoginExitoso = { destinoRol ->
                                    mainViewModel.navigateTo(
                                        screen = destinoRol,
                                        popUpToRoute = Screen.Login,
                                        inclusive = true
                                    )
                                }
                            )
                        }

                        composable(route = Screen.HomeAdmin.route) {
                            HomeAdminScreen(
                                loginViewModel = loginViewModel,
                                onLogout = {
                                    mainViewModel.navigateTo(
                                        screen = Screen.Login,
                                        popUpToRoute = Screen.HomeAdmin,
                                        inclusive = true
                                    )
                                }
                            )
                        }

                        composable(route = Screen.HomeSupervisor.route) {
                            HomeSupervisorScreen(
                                loginViewModel = loginViewModel,
                                onLogout = {
                                    mainViewModel.navigateTo(
                                        screen = Screen.Login,
                                        popUpToRoute = Screen.HomeSupervisor,
                                        inclusive = true
                                    )
                                }
                            )
                        }

                        composable(route = Screen.HomeOperador.route) {
                            HomeOperadorScreen(
                                loginViewModel = loginViewModel,
                                onLogout = {
                                    mainViewModel.navigateTo(
                                        screen = Screen.Login,
                                        popUpToRoute = Screen.HomeOperador,
                                        inclusive = true
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}