package ru.fefu.task3.data.model

import com.google.gson.annotations.SerializedName

data class AnimeBase(
    val id: Long,
    val name: String,
    val russian: String?,
    val image: AnimeImage?,
    val score: String?,
    val kind: String?,
    val status: String?
) {
    fun getImageUrl(): String? = image?.original?.let { "https://shikimori.one$it" }
    
    fun getDisplayName(): String = if (!russian.isNullOrBlank()) russian else name

    fun getRussianKind(): String = when (kind?.lowercase()) {
        "tv" -> "TV Сериал"
        "movie" -> "Фильм"
        "ova" -> "OVA"
        "ona" -> "ONA"
        "special" -> "Спешл"
        "music" -> "Клип"
        else -> kind ?: "Неизвестно"
    }

    fun getRussianStatus(): String = when (status?.lowercase()) {
        "released" -> "Вышло"
        "ongoing" -> "Онгоинг"
        "announced" -> "Анонсировано"
        else -> status ?: "Неизвестно"
    }
}

data class AnimeDetails(
    val id: Long,
    val name: String,
    val russian: String?,
    val image: AnimeImage?,
    val score: String?,
    val kind: String?,
    val status: String?,
    val description: String?,
    @SerializedName("description_html") val descriptionHtml: String?,
    val episodes: Int?,
    @SerializedName("aired_on") val airedOn: String?,
    val genres: List<Genre>?
) {
    fun getImageUrl(): String? = image?.original?.let { "https://shikimori.one$it" }
    
    fun getDisplayName(): String = if (!russian.isNullOrBlank()) russian else name

    fun getCleanDescription(): String? {
        val textToClean = description ?: descriptionHtml?.replace(Regex("<.*?>"), "")
        return textToClean?.replace(Regex("""\[.*?\]"""), "")
    }

    fun getRussianKind(): String = when (kind?.lowercase()) {
        "tv" -> "TV Сериал"
        "movie" -> "Фильм"
        "ova" -> "OVA"
        "ona" -> "ONA"
        "special" -> "Спешл"
        "music" -> "Клип"
        else -> kind ?: "Неизвестно"
    }

    fun getRussianStatus(): String = when (status?.lowercase()) {
        "released" -> "Вышло"
        "ongoing" -> "Онгоинг"
        "announced" -> "Анонсировано"
        else -> status ?: "Неизвестно"
    }
}

data class AnimeImage(
    val original: String?,
    val preview: String?,
    val x96: String?,
    val x48: String?
)

data class Genre(
    val id: Long,
    val name: String,
    val russian: String?
)