package ru.fefu.task3.data

import ru.fefu.task3.data.db.AnimeEntity
import ru.fefu.task3.data.db.UserEntity
import ru.fefu.task3.data.db.AnimeNoteEntity
import ru.fefu.task3.domain.model.AnimeBase
import ru.fefu.task3.domain.model.AnimeDetails
import ru.fefu.task3.domain.model.User
import ru.fefu.task3.domain.model.AnimeNote

fun AnimeEntity.toAnimeBase() = AnimeBase(
    id = id,
    name = name,
    russian = russian,
    imageUrl = imageUrl, // в БД уже лежит абсолютная ссылка
    score = score,
    kind = kind,
    status = status,
    isFavourite = true
)

fun AnimeDetails.toEntity(userId: Long) = AnimeEntity(
    id = id,
    userId = userId,
    name = name,
    russian = russian,
    imageUrl = imageUrl,
    score = score,
    kind = kind,
    status = status
)

private val gson = com.google.gson.Gson()

fun AnimeDetails.toCachedEntity() = ru.fefu.task3.data.db.CachedAnimeDetailsEntity(
    animeId = id,
    name = name,
    russian = russian,
    imageUrl = imageUrl,
    score = score,
    kind = kind,
    status = status,
    description = description,
    episodes = episodes,
    airedOn = airedOn,
    genresJson = gson.toJson(genres),
    cachedAt = System.currentTimeMillis()
)

fun ru.fefu.task3.data.db.CachedAnimeDetailsEntity.toDomain(): AnimeDetails {
    val genresType = object : com.google.gson.reflect.TypeToken<List<ru.fefu.task3.domain.model.Genre>?>() {}.type
    val genresList: List<ru.fefu.task3.domain.model.Genre>? = gson.fromJson(genresJson, genresType)
    return AnimeDetails(
        id = animeId,
        name = name,
        russian = russian,
        imageUrl = imageUrl,
        score = score,
        kind = kind,
        status = status,
        description = description,
        descriptionHtml = null,
        episodes = episodes,
        airedOn = airedOn,
        genres = genresList
    )
}

fun ru.fefu.task3.data.db.CachedAnimeDetailsEntity.toAnimeBase() = AnimeBase(
    id = animeId,
    name = name,
    russian = russian,
    imageUrl = imageUrl,
    score = score,
    kind = kind,
    status = status,
    isFavourite = false
)

fun UserEntity.toDomain() = User(
    id = id,
    name = name,
    avatarUrl = avatarUrl
)

fun User.toEntity() = UserEntity(
    id = id,
    name = name,
    avatarUrl = avatarUrl
)

fun AnimeNoteEntity.toDomain() = AnimeNote(
    id = id,
    userId = userId,
    animeId = animeId,
    noteText = noteText,
    rating = rating,
    updatedAt = updatedAt
)

fun AnimeNote.toEntity() = AnimeNoteEntity(
    id = id,
    userId = userId,
    animeId = animeId,
    noteText = noteText,
    rating = rating,
    updatedAt = updatedAt
)