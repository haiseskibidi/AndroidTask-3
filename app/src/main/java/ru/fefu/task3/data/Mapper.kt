package ru.fefu.task3.data

import ru.fefu.task3.data.db.AnimeEntity
import ru.fefu.task3.data.model.AnimeBase
import ru.fefu.task3.data.model.AnimeDetails
import ru.fefu.task3.data.model.AnimeImage

fun AnimeEntity.toAnimeBase() = AnimeBase(
    id = id,
    name = name,
    russian = russian,
    image = imageUrl?.let { url -> 
        AnimeImage(
            original = url.removePrefix("https://shikimori.one"),
            preview = null,
            x96 = null,
            x48 = null
        )
    },
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