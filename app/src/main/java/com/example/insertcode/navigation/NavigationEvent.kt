package com.example.insertcode.navigation

// Representa los distintos tipos de eventos de navegación emitidos por el ViewModel
sealed class NavigationEvent {
    data class NavigateTo(
        val route: Screen,
        val popupToRoute: Screen? = null,
        val inclusive: Boolean = false,
        val singleTop: Boolean = false
    ) : NavigationEvent()

    data object PopBackStack : NavigationEvent()
    data object NavigateUp : NavigationEvent()
}