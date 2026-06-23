package ru.fefu.task3.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ru.fefu.task3.R

@Composable
fun String?.toRussianKind(): String = when (this?.lowercase()) {
    "tv" -> stringResource(R.string.kind_tv)
    "movie" -> stringResource(R.string.kind_movie)
    "ova" -> stringResource(R.string.kind_ova)
    "ona" -> stringResource(R.string.kind_ona)
    "special" -> stringResource(R.string.kind_special)
    "music" -> stringResource(R.string.kind_music)
    else -> this ?: stringResource(R.string.kind_unknown)
}

@Composable
fun String?.toRussianStatus(): String = when (this?.lowercase()) {
    "released" -> stringResource(R.string.status_released)
    "ongoing" -> stringResource(R.string.status_ongoing)
    "announced" -> stringResource(R.string.status_announced)
    else -> this ?: stringResource(R.string.status_unknown)
}
