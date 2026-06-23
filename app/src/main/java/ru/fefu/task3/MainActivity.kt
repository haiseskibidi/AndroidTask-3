package ru.fefu.task3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
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
import ru.fefu.task3.ui.screens.RecentScreen
import ru.fefu.task3.ui.screens.SettingsScreen
import ru.fefu.task3.ui.theme.Task3Theme
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: AnimeViewModel = hiltViewModel()
            val isDarkThemePref by viewModel.isDarkTheme.collectAsStateWithLifecycle()
            val darkTheme = isDarkThemePref ?: true

            Task3Theme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = Screen.List.route) {
                    composable(Screen.List.route) {
                        val uiState by viewModel.listUiState.collectAsStateWithLifecycle()
                        val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

                        ListScreen(
                            uiState = uiState,
                            searchQuery = searchQuery,
                            onEvent = viewModel::onListEvent,
                            onAnimeClick = { id -> navController.navigate(Screen.Details.createRoute(id)) },
                            onFavouritesClick = { navController.navigate(Screen.Favourites.route) },
                            onRecentClick = { navController.navigate(Screen.Recent.route) },
                            onSettingsClick = { navController.navigate(Screen.Settings.route) }
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

                    composable(Screen.Recent.route) {
                        val recentAnimes by viewModel.recentAnimes.collectAsStateWithLifecycle()

                        RecentScreen(
                            recentAnimes = recentAnimes,
                            onAnimeClick = { id -> navController.navigate(Screen.Details.createRoute(id)) },
                            onBackClick = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.Settings.route) {
                        val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
                        val activeUserId by viewModel.activeUserId.collectAsStateWithLifecycle()

                        SettingsScreen(
                            currentTheme = darkTheme,
                            allUsers = allUsers,
                            activeUserId = activeUserId,
                            onThemeChange = viewModel::toggleTheme,
                            onUserSelect = viewModel::selectUser,
                            onUserCreate = viewModel::createUser,
                            onUserDelete = viewModel::deleteUser,
                            onClearHistory = viewModel::clearHistory,
                            onBackClick = { navController.popBackStack() }
                        )
                    }

                    composable(
                        route = Screen.Details.route,
                        arguments = listOf(navArgument("animeId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val animeId = backStackEntry.arguments?.getLong("animeId") ?: return@composable
                        val uiState by viewModel.detailUiState.collectAsStateWithLifecycle()
                        val note by viewModel.getNoteForAnime(animeId).collectAsStateWithLifecycle(initialValue = null)

                        DetailScreen(
                            animeId = animeId,
                            uiState = uiState,
                            note = note,
                            onLoad = viewModel::loadAnimeDetails,
                            onRetryClick = { viewModel.loadAnimeDetails(animeId) },
                            onBackClick = { navController.popBackStack() },
                            onToggleFavourite = viewModel::toggleFavourite,
                            onSaveNote = { text, rating -> viewModel.saveNote(animeId, text, rating) }
                        )
                    }
                }
            }
        }
    }
}
}
