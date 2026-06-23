package ru.fefu.task3.domain.model

interface AnimeModel {
    val id: Long
    val name: String
    val russian: String?
    val imageUrl: String?
    val score: String?
    val kind: String?
    val status: String?

    fun getDisplayName(): String = russian?.takeIf { it.isNotBlank() } ?: name
}

data class AnimeBase(
    override val id: Long,
    override val name: String,
    override val russian: String?,
    override val imageUrl: String?,
    override val score: String?,
    override val kind: String?,
    override val status: String?,
    val isFavourite: Boolean = false
) : AnimeModel

data class AnimeDetails(
    override val id: Long,
    override val name: String,
    override val russian: String?,
    override val imageUrl: String?,
    override val score: String?,
    override val kind: String?,
    override val status: String?,
    val description: String?,
    val descriptionHtml: String?,
    val episodes: Int?,
    val airedOn: String?,
    val genres: List<Genre>?
) : AnimeModel

data class Genre(
    val id: Long,
    val name: String,
    val russian: String?
)