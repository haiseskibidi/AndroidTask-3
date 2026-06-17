package ru.fefu.task3.data.network.dto

import com.google.gson.annotations.SerializedName

data class AnimeDto(
    val id: Long,
    val name: String,
    val russian: String?,
    val image: AnimeImageDto?,
    val score: String?,
    val kind: String?,
    val status: String?
)

data class AnimeDetailsDto(
    val id: Long,
    val name: String,
    val russian: String?,
    val image: AnimeImageDto?,
    val score: String?,
    val kind: String?,
    val status: String?,
    val description: String?,
    @SerializedName("description_html") val descriptionHtml: String?,
    val episodes: Int?,
    @SerializedName("aired_on") val airedOn: String?,
    val genres: List<GenreDto>?
)

data class AnimeImageDto(
    val original: String?,
    val preview: String?,
    val x96: String?,
    val x48: String?
)

data class GenreDto(
    val id: Long,
    val name: String,
    val russian: String?
)
