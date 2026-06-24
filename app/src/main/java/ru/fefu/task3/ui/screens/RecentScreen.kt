package ru.fefu.task3.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ru.fefu.task3.domain.model.AnimeBase
import ru.fefu.task3.ui.components.AnimeItem
import androidx.compose.ui.res.stringResource
import ru.fefu.task3.R
import ru.fefu.task3.ui.theme.spacing

import androidx.compose.ui.tooling.preview.Preview
import ru.fefu.task3.ui.theme.Task3Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentScreen(
    recentAnimes: List<AnimeBase>,
    onAnimeClick: (Long) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.history_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        if (recentAnimes.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.history_empty))
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(MaterialTheme.spacing.spacing6),
                modifier = Modifier.fillMaxSize().padding(padding)
            ) {
                items(recentAnimes, key = { it.id }) { anime ->
                    AnimeItem(
                        anime = anime,
                        onClick = { onAnimeClick(anime.id) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecentScreenPreview() {
    Task3Theme {
        RecentScreen(
            recentAnimes = listOf(
                AnimeBase(1, "Naruto", "Наруто", null, "8.3", "tv", "released"),
                AnimeBase(2, "One Piece", "Ван Пис", null, "8.6", "tv", "ongoing")
            ),
            onAnimeClick = {},
            onBackClick = {}
        )
    }
}
