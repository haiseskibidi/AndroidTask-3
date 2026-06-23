package ru.fefu.task3.ui.navigation

sealed class Screen(val route: String) {
    object List : Screen("list")
    object Favourites : Screen("favourites")
    object Settings : Screen("settings")
    object Recent : Screen("recent")
    object Details : Screen("details/{animeId}") {
        fun createRoute(animeId: Long) = "details/$animeId"
    }
}