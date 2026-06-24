package ru.fefu.task3.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ru.fefu.task3.R
import ru.fefu.task3.ui.ListEvent
import ru.fefu.task3.ui.ListUiState
import ru.fefu.task3.ui.components.AnimeItem
import ru.fefu.task3.ui.theme.spacing

import androidx.compose.ui.tooling.preview.Preview
import ru.fefu.task3.domain.model.AnimeBase
import ru.fefu.task3.ui.theme.Task3Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    uiState: ListUiState,
    searchQuery: String,
    onEvent: (ListEvent) -> Unit,
    onAnimeClick: (Long) -> Unit,
    onFavouritesClick: () -> Unit,
    onRecentClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = onRecentClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.List,
                            contentDescription = stringResource(R.string.history_title),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onFavouritesClick) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = stringResource(R.string.favourites_title),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = stringResource(R.string.settings_title),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { onEvent(ListEvent.SearchQueryChanged(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MaterialTheme.spacing.spacing12),
                placeholder = { Text(stringResource(R.string.search_placeholder)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier.clickable { onEvent(ListEvent.SearchQueryChanged("")) }
                        )
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(MaterialTheme.spacing.spacing12),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search,
                    keyboardType = KeyboardType.Text,
                    autoCorrect = true
                ),
                keyboardActions = KeyboardActions(
                    onSearch = { focusManager.clearFocus() }
                )
            )

            when (val state = uiState) {
                is ListUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is ListUiState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(stringResource(R.string.error_prefix, state.message), color = MaterialTheme.colorScheme.error)
                        Button(onClick = { onEvent(ListEvent.Retry) }) {
                            Text(stringResource(R.string.retry_button))
                        }
                    }
                }
                is ListUiState.Empty -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.no_anime_found))
                    }
                }
                is ListUiState.Success -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(MaterialTheme.spacing.spacing6),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.animes, key = { it.id }) { anime ->
                            AnimeItem(anime = anime, onClick = { onAnimeClick(anime.id) })
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ListScreenPreview() {
    Task3Theme {
        ListScreen(
            uiState = ListUiState.Success(
                listOf(
                    AnimeBase(1, "Naruto", "Наруто", null, "8.3", "tv", "released"),
                    AnimeBase(2, "One Piece", "Ван Пис", null, "8.6", "tv", "ongoing")
                )
            ),
            searchQuery = "",
            onEvent = {},
            onAnimeClick = {},
            onFavouritesClick = {},
            onRecentClick = {},
            onSettingsClick = {}
        )
    }
}
