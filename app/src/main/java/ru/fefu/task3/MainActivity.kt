package ru.fefu.task3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import ru.fefu.task3.ui.AnimeViewModel
import ru.fefu.task3.ui.navigation.Screen
import ru.fefu.task3.ui.screens.DetailScreen
import ru.fefu.task3.ui.screens.FavouritesScreen
import ru.fefu.task3.ui.screens.ListScreen
import ru.fefu.task3.ui.theme.Task3Theme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Task3Theme {
                val navController = rememberNavController()
                val viewModel: AnimeViewModel = hiltViewModel()

                NavHost(navController = navController, startDestination = Screen.List.route) {
                    composable(Screen.List.route) {
                        val uiState by viewModel.listUiState.collectAsStateWithLifecycle()
                        val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

                        ListScreen(
                            uiState = uiState,
                            searchQuery = searchQuery,
                            onEvent = viewModel::onListEvent,
                            onAnimeClick = { id -> navController.navigate(Screen.Details.createRoute(id)) },
                            onFavouritesClick = { navController.navigate(Screen.Favourites.route) }
                        )
                    }

                    composable(Screen.Favourites.route) {
                        val favourites by viewModel.favouritesList.collectAsStateWithLifecycle()

                        FavouritesScreen(
                            favourites = favourites,
                            onBackClick = { navController.popBackStack() },
                            onAnimeClick = { id -> navController.navigate(Screen.Details.createRoute(id)) }
                        )
                    }

                    composable(
                        route = Screen.Details.route,
                        arguments = listOf(navArgument("animeId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val animeId = backStackEntry.arguments?.getLong("animeId") ?: return@composable
                        val uiState by viewModel.detailUiState.collectAsStateWithLifecycle()

                        DetailScreen(
                            animeId = animeId,
                            uiState = uiState,
                            onLoad = viewModel::loadAnimeDetails,
                            onRetryClick = { viewModel.loadAnimeDetails(animeId) },
                            onBackClick = { navController.popBackStack() },
                            onToggleFavourite = viewModel::toggleFavourite
                        )
                    }
                }
            }
        }
    }
}
