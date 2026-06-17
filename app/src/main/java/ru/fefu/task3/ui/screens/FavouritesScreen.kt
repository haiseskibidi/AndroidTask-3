package ru.fefu.task3.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ru.fefu.task3.data.model.AnimeBase
import ru.fefu.task3.ui.components.AnimeItem

import androidx.compose.ui.res.stringResource
import ru.fefu.task3.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouritesScreen(
    favourites: List<AnimeBase>,
    onBackClick: () -> Unit,
    onAnimeClick: (Long) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.favourites_title)) },
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
                Text(stringResource(R.string.favourites_empty))
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