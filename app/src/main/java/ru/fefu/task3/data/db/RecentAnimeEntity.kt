package ru.fefu.task3.data.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "recent_anime",
    primaryKeys = ["userId", "animeId"],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId"])]
)
data class RecentAnimeEntity(
    val userId: Long,
    val animeId: Long,
    val viewedAt: Long
)
