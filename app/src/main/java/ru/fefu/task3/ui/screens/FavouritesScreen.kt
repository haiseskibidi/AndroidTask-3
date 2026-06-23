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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.fefu.task3.domain.model.AnimeBase
import ru.fefu.task3.ui.components.AnimeItem
import ru.fefu.task3.ui.theme.spacing

import androidx.compose.ui.res.stringResource
import ru.fefu.task3.R

enum class SortOption(val titleRes: Int) {
    DEFAULT(R.string.sort_default),
    BY_RATING(R.string.sort_by_rating),
    BY_NAME(R.string.sort_by_name)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouritesScreen(
    favourites: List<AnimeBase>,
    onBackClick: () -> Unit,
    onAnimeClick: (Long) -> Unit
) {
    var currentSortOption by remember { mutableStateOf(SortOption.DEFAULT) }

    val sortedFavourites = remember(favourites, currentSortOption) {
        when (currentSortOption) {
            SortOption.DEFAULT -> favourites
            SortOption.BY_RATING -> favourites.sortedWith(
                compareByDescending<AnimeBase> { it.userRating ?: 0f }
                    .thenBy { it.getDisplayName() }
            )
            SortOption.BY_NAME -> favourites.sortedBy { it.getDisplayName() }
        }
    }

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
            Column(modifier = Modifier.padding(padding).fillMaxSize()) {
                androidx.compose.foundation.lazy.LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = MaterialTheme.spacing.spacing12,
                            vertical = MaterialTheme.spacing.extraSmall
                        ),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    item {
                        Text(stringResource(R.string.sort_label), style = MaterialTheme.typography.labelLarge)
                    }
                    items(SortOption.values().toList()) { option ->
                        FilterChip(
                            selected = currentSortOption == option,
                            onClick = { currentSortOption = option },
                            label = { Text(stringResource(option.titleRes)) }
                        )
                    }
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(MaterialTheme.spacing.spacing6),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(sortedFavourites, key = { it.id }) { anime ->
                        AnimeItem(anime = anime, onClick = { onAnimeClick(anime.id) })
                    }
                }
            }
        }
    }
}