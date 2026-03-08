package ru.fefu.task3.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourites")
data class AnimeEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val russian: String?,
    val imageUrl: String?,
    val score: String?,
    val kind: String?,
    val status: String?
)