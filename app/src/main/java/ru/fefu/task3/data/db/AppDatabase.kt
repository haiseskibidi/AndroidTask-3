package ru.fefu.task3.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        AnimeEntity::class,
        UserEntity::class,
        RecentAnimeEntity::class,
        AnimeNoteEntity::class,
        CachedAnimeDetailsEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun animeDao(): AnimeDao
}