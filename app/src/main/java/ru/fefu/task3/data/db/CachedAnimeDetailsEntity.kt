package ru.fefu.task3.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_anime_details")
data class CachedAnimeDetailsEntity(
    @PrimaryKey val animeId: Long,
    val name: String,
    val russian: String?,
    val imageUrl: String?,
    val score: String?,
    val kind: String?,
    val status: String?,
    val description: String?,
    val episodes: Int?,
    val airedOn: String?,
    val genresJson: String?,
    val cachedAt: Long
)
