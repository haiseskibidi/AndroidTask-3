package ru.fefu.task3.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import ru.fefu.task3.R
import ru.fefu.task3.domain.model.AnimeDetails
import ru.fefu.task3.ui.DetailUiState
import ru.fefu.task3.ui.components.toRussianKind
import ru.fefu.task3.ui.components.toRussianStatus
import ru.fefu.task3.domain.model.AnimeNote

import androidx.compose.ui.tooling.preview.Preview
import ru.fefu.task3.ui.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    animeId: Long,
    uiState: DetailUiState,
    note: AnimeNote?,
    onLoad: (Long) -> Unit,
    onRetryClick: () -> Unit,
    onBackClick: () -> Unit,
    onToggleFavourite: (AnimeDetails) -> Unit,
    onSaveNote: (String, Float?) -> Unit
) {
    LaunchedEffect(animeId) {
        onLoad(animeId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.details_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    if (uiState is DetailUiState.Success) {
                        IconButton(onClick = { onToggleFavourite(uiState.anime) }) {
                            Icon(
                                imageVector = if (uiState.isFavourite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = if (uiState.isFavourite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is DetailUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is DetailUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(stringResource(R.string.error_prefix, state.message), color = MaterialTheme.colorScheme.error)
                        Button(onClick = onRetryClick) {
                            Text(stringResource(R.string.retry_button))
                        }
                    }
                }
                is DetailUiState.Success -> {
                    val anime = state.anime
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        AsyncImage(
                            model = anime.imageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(MaterialTheme.spacing.detailsImageHeight),
                            contentScale = ContentScale.Crop
                        )
                        Column(modifier = Modifier.padding(MaterialTheme.spacing.spacing20)) {
                            Text(
                                text = anime.getDisplayName(),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                            
                            Row(
                                modifier = Modifier.padding(vertical = MaterialTheme.spacing.small),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = RoundedCornerShape(MaterialTheme.spacing.small)
                                ) {
                                    Text(
                                        text = anime.kind.toRussianKind(),
                                        modifier = Modifier.padding(horizontal = MaterialTheme.spacing.small, vertical = MaterialTheme.spacing.extraSmall),
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                                Spacer(modifier = Modifier.width(MaterialTheme.spacing.small))
                                Surface(
                                    color = MaterialTheme.colorScheme.tertiaryContainer,
                                    shape = RoundedCornerShape(MaterialTheme.spacing.small)
                                ) {
                                    Text(
                                        text = anime.status.toRussianStatus(),
                                        modifier = Modifier.padding(horizontal = MaterialTheme.spacing.small, vertical = MaterialTheme.spacing.extraSmall),
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = MaterialTheme.spacing.small))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                InfoColumn(stringResource(R.string.info_label_rating), anime.score ?: "N/A")
                                InfoColumn(stringResource(R.string.info_label_episodes), anime.episodes?.toString() ?: "?")
                                InfoColumn(stringResource(R.string.info_label_year), anime.airedOn?.take(4) ?: "?")
                            }

                            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))
                            Text(
                                stringResource(R.string.description_label),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
                            Text(
                                text = anime.description ?: stringResource(R.string.description_missing),
                                style = MaterialTheme.typography.bodyMedium,
                                lineHeight = 22.sp
                            )

                            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))
                            NoteSection(
                                note = note,
                                onSaveNote = onSaveNote
                            )
                            
                            if (!anime.genres.isNullOrEmpty()) {
                                Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))
                                Text(
                                    stringResource(R.string.genres_label),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = anime.genres.joinToString { it.russian ?: it.name },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                                    modifier = Modifier.padding(top = MaterialTheme.spacing.small),
                                    lineHeight = 22.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

