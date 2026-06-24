package ru.fefu.task3.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import android.widget.Toast
import ru.fefu.task3.ui.DetailUiState
import ru.fefu.task3.domain.model.AnimeDetails
import ru.fefu.task3.ui.theme.Task3Theme
import ru.fefu.task3.domain.model.AnimeNote
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.res.stringResource
import ru.fefu.task3.R
import ru.fefu.task3.ui.theme.spacing

@Composable
fun InfoColumn(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun NoteSection(
    note: AnimeNote?,
    onSaveNote: (String, Float?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var rating by remember(note) { mutableStateOf(note?.rating) }

    Column(modifier = modifier) {
        Text(
            stringResource(R.string.my_note),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = MaterialTheme.spacing.extraSmall)
        ) {
            Text(stringResource(R.string.my_rating_label), style = MaterialTheme.typography.bodyLarge)
            Row {
                for (i in 1..5) {
                    val starRating = i.toFloat()
                    IconButton(
                        onClick = {
                            rating = if (rating == starRating) null else starRating
                        },
                        modifier = Modifier.size(MaterialTheme.spacing.starSize)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (rating != null && rating!! >= starRating) {
                                androidx.compose.ui.graphics.Color(0xFFFFC107)
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                            }
                        )
                    }
                }
            }
        }
        
        var noteText by remember(note) { mutableStateOf(note?.noteText ?: "") }
        
        OutlinedTextField(
            value = noteText,
            onValueChange = { noteText = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(stringResource(R.string.note_placeholder)) }
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
        Button(
            onClick = {
                onSaveNote(noteText, rating)
                keyboardController?.hide()
                Toast.makeText(context, context.getString(R.string.note_saved_toast), Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(stringResource(R.string.save_button))
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DetailScreenPreview() {
    Task3Theme {
        DetailScreen(
            animeId = 1,
            uiState = DetailUiState.Success(
                anime = AnimeDetails(
                    id = 1,
                    name = "Naruto",
                    russian = "Наруто",
                    imageUrl = null,
                    score = "8.3",
                    kind = "tv",
                    status = "released",
                    description = "Description of Naruto anime.",
                    descriptionHtml = null,
                    episodes = 220,
                    airedOn = "2002-10-03",
                    genres = emptyList()
                ),
                isFavourite = true
            ),
            note = null,
            onLoad = {},
            onRetryClick = {},
            onBackClick = {},
            onToggleFavourite = {},
            onSaveNote = { _, _ -> }
        )
    }
}

