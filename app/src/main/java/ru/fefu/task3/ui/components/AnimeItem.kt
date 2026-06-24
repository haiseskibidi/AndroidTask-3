package ru.fefu.task3.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import ru.fefu.task3.domain.model.AnimeBase
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star

import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.tooling.preview.Preview
import ru.fefu.task3.ui.theme.Task3Theme
import ru.fefu.task3.ui.theme.BurgundyPrimary
import ru.fefu.task3.ui.theme.spacing

@Composable
fun AnimeItem(
    anime: AnimeBase,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.spacing6)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(MaterialTheme.spacing.cardHeight)
        ) {
            AsyncImage(
                model = anime.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(MaterialTheme.spacing.spacing6)),
                contentScale = ContentScale.Crop
            )

            // rating badge in top-left (mangalib style, overlapping)
            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(
                        x = -MaterialTheme.spacing.extraSmall,
                        y = -MaterialTheme.spacing.extraSmall
                    ),
                shape = RoundedCornerShape(MaterialTheme.spacing.extraSmall),
                color = BurgundyPrimary,
                shadowElevation = MaterialTheme.spacing.spacing2
            ) {
                Text(
                    text = anime.score ?: "—",
                    modifier = Modifier.padding(
                        horizontal = MaterialTheme.spacing.spacing6,
                        vertical = MaterialTheme.spacing.spacing2 // ponytail: using spacing2 for vertical padding as approximation of 3.dp
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.spacing6))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = anime.getDisplayName(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 18.sp,
                modifier = Modifier.weight(1f)
            )

            if (anime.isFavourite) {
                Spacer(modifier = Modifier.width(MaterialTheme.spacing.small))
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = BurgundyPrimary,
                    modifier = Modifier
                        .size(MaterialTheme.spacing.medium)
                        .padding(top = MaterialTheme.spacing.spacing2)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = anime.kind.toRussianKind(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            if (anime.userRating != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(MaterialTheme.spacing.starSizeSmall)
                    )
                    Spacer(modifier = Modifier.width(MaterialTheme.spacing.spacing2))
                    Text(
                        text = String.format("%.0f", anime.userRating),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFC107)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnimeItemPreview() {
    Task3Theme {
        AnimeItem(
            anime = AnimeBase(
                id = 1,
                name = "Cowboy Bebop",
                russian = "Ковбой Бибоп",
                imageUrl = "https://shikimori.one/system/animes/original/1.jpg",
                score = "8.75",
                kind = "tv",
                status = "released"
            ),
            onClick = {}
        )
    }
}
