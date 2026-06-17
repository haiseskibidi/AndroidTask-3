package ru.fefu.task3.data.network.dto

import ru.fefu.task3.BuildConfig
import ru.fefu.task3.domain.model.AnimeBase
import ru.fefu.task3.domain.model.AnimeDetails
import ru.fefu.task3.domain.model.Genre

private fun String?.toAbsoluteUrl(): String? {
    val path = this ?: return null
    return if (path.startsWith("/")) {
        "${BuildConfig.IMAGE_BASE_URL}$path"
    } else {
        path
    }
}

fun AnimeDto.toDomain() = AnimeBase(
    id = id,
    name = name,
    russian = russian,
    imageUrl = image?.original.toAbsoluteUrl(),
    score = score,
    kind = kind,
    status = status
)

private fun String?.cleanDescription(): String? {
    val raw = this ?: return null
    return raw.replace(Regex("""\[[^\]]*\]"""), "") // удаляем любые теги в квадратных скобках
        .replace("[", "") // убираем возможные одиночные скобки
        .replace("]", "")
        .replace(Regex("""\s+"""), " ") // убираем лишние пробелы
        .trim()
}

fun AnimeDetailsDto.toDomain() = AnimeDetails(
    id = id,
    name = name,
    russian = russian,
    imageUrl = image?.original.toAbsoluteUrl(),
    score = score,
    kind = kind,
    status = status,
    description = (description ?: descriptionHtml?.replace(Regex("<.*?>"), "")).cleanDescription(),
    descriptionHtml = null, // больше не нужно в домене
    episodes = episodes,
    airedOn = airedOn,
    genres = genres?.map { Genre(it.id, it.name, it.russian) }
)