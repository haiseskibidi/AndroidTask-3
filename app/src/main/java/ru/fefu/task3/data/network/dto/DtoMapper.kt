package ru.fefu.task3.data.network.dto

import ru.fefu.task3.data.model.AnimeBase
import ru.fefu.task3.data.model.AnimeDetails
import ru.fefu.task3.data.model.AnimeImage
import ru.fefu.task3.data.model.Genre
import ru.fefu.task3.BuildConfig

private fun String.toAbsoluteUrl(): String {
    return if (this.startsWith("/")) {
        "${BuildConfig.IMAGE_BASE_URL}$this"
    } else {
        this
    }
}

fun AnimeDto.toDomain() = AnimeBase(
    id = id,
    name = name,
    russian = russian,
    image = image?.let { img ->
        AnimeImage(
            original = img.original?.toAbsoluteUrl(),
            preview = img.preview?.toAbsoluteUrl(),
            x96 = img.x96?.toAbsoluteUrl(),
            x48 = img.x48?.toAbsoluteUrl()
        )
    },
    score = score,
    kind = kind,
    status = status
)

fun AnimeDetailsDto.toDomain() = AnimeDetails(
    id = id,
    name = name,
    russian = russian,
    image = image?.let { img ->
        AnimeImage(
            original = img.original?.toAbsoluteUrl(),
            preview = img.preview?.toAbsoluteUrl(),
            x96 = img.x96?.toAbsoluteUrl(),
            x48 = img.x48?.toAbsoluteUrl()
        )
    },
    score = score,
    kind = kind,
    status = status,
    description = description,
    descriptionHtml = descriptionHtml,
    episodes = episodes,
    airedOn = airedOn,
    genres = genres?.map { Genre(it.id, it.name, it.russian) }
)