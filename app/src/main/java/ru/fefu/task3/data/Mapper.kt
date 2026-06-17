package ru.fefu.task3.data

import ru.fefu.task3.data.db.AnimeEntity
import ru.fefu.task3.data.model.AnimeBase
import ru.fefu.task3.data.model.AnimeDetails
import ru.fefu.task3.data.model.AnimeImage

import ru.fefu.task3.BuildConfig

private fun String.toAbsoluteUrl(): String {
    return if (this.startsWith("/")) {
        "${BuildConfig.IMAGE_BASE_URL}$this"
    } else {
        this
    }
}

fun AnimeEntity.toAnimeBase() = AnimeBase(
    id = id,
    name = name,
    russian = russian,
    image = imageUrl?.let { AnimeImage(it.toAbsoluteUrl().removePrefix(BuildConfig.IMAGE_BASE_URL), null, null, null) },
    score = score,
    kind = kind,
    status = status
)

fun AnimeDetails.toEntity() = AnimeEntity(
    id = id,
    name = name,
    russian = russian,
    imageUrl = getImageUrl(),
    score = score,
    kind = kind,
    status = status
)