package com.example.insertcode.navigation

// Sealed class para definir rutas tipo-safe en la navegación
sealed class Screen(val route: String) {
    // Rutas simples utilizando 'data object' (singleton tipo-safe)
    data object Home : Screen(route = "home_page")
    data object Profile : Screen(route = "profile_page")
    data object Settings : Screen(route = "settings_page")

    // Ejemplo de ruta con argumento (referencial)
    data class Detail(val itemId: String) : Screen(route = "detail_page/$itemId") {
        fun buildRoute(): String = route.replace("{itemId}", itemId)
    }
}