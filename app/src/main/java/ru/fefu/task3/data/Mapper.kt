package ru.fefu.task3.data

import ru.fefu.task3.data.db.AnimeEntity
import ru.fefu.task3.domain.model.AnimeBase
import ru.fefu.task3.domain.model.AnimeDetails

fun AnimeEntity.toAnimeBase() = AnimeBase(
    id = id,
    name = name,
    russian = russian,
    imageUrl = imageUrl, // в БД уже лежит абсолютная ссылка
    score = score,
    kind = kind,
    status = status
)

fun AnimeDetails.toEntity() = AnimeEntity(
    id = id,
    name = name,
    russian = russian,
    imageUrl = imageUrl,
    score = score,
    kind = kind,
    status = status
)