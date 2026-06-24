package ru.fefu.task3.data.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "favourites",
    primaryKeys = ["userId", "id"],
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
data class AnimeEntity(
    val id: Long,
    val userId: Long,
    val name: String,
    val russian: String?,
    val imageUrl: String?,
    val score: String?,
    val kind: String?,
    val status: String?
)