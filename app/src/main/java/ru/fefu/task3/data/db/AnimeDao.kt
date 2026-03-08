package ru.fefu.task3.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimeDao {
    @Query("SELECT * FROM favourites")
    fun getAllFavourites(): Flow<List<AnimeEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favourites WHERE id = :id)")
    fun isFavourite(id: Long): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM favourites WHERE id = :id)")
    suspend fun isFavouriteSync(id: Long): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToFavourites(anime: AnimeEntity)

    @Query("DELETE FROM favourites WHERE id = :id")
    suspend fun removeFromFavourites(id: Long)
}