package ru.fefu.task3.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ru.fefu.task3.ui.AnimeViewModel
import ru.fefu.task3.ui.components.AnimeItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouritesScreen(
    viewModel: AnimeViewModel,
    onBackClick: () -> Unit,
    onAnimeClick: (Long) -> Unit
) {
    val favourites by viewModel.favouritesList.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Избранное") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        if (favourites.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("В избранном пока пусто.")
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(favourites) { anime ->
                    AnimeItem(anime = anime, onClick = { onAnimeClick(anime.id) })
                }
            }
        }
    }
}