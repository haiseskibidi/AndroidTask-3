package ru.fefu.task3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ru.fefu.task3.ui.AnimeViewModel
import ru.fefu.task3.ui.navigation.Screen
import ru.fefu.task3.ui.screens.DetailScreen
import ru.fefu.task3.ui.screens.FavouritesScreen
import ru.fefu.task3.ui.screens.ListScreen
import ru.fefu.task3.ui.theme.Task3Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Task3Theme {
                val navController = rememberNavController()
                val viewModel: AnimeViewModel = viewModel()

                NavHost(navController = navController, startDestination = Screen.List.route) {
                    composable(Screen.List.route) {
                        ListScreen(
                            viewModel = viewModel,
                            onAnimeClick = { id -> navController.navigate(Screen.Details.createRoute(id)) },
                            onFavouritesClick = { navController.navigate(Screen.Favourites.route) }
                        )
                    }

                    composable(Screen.Favourites.route) {
                        FavouritesScreen(
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() },
                            onAnimeClick = { id -> navController.navigate(Screen.Details.createRoute(id)) }
                        )
                    }

                    composable(
                        route = Screen.Details.route,
                        arguments = listOf(navArgument("animeId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val animeId = backStackEntry.arguments?.getLong("animeId") ?: return@composable
                        DetailScreen(
                            animeId = animeId,
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
