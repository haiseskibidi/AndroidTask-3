package ru.fefu.task3.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimeDao {

    // --- избранное (favourites) ---
    @Query("SELECT * FROM favourites WHERE userId = :userId")
    fun getAllFavourites(userId: Long): Flow<List<AnimeEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favourites WHERE userId = :userId AND id = :id)")
    fun isFavourite(userId: Long, id: Long): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToFavourites(anime: AnimeEntity)

    @Query("DELETE FROM favourites WHERE userId = :userId AND id = :id")
    suspend fun removeFromFavourites(userId: Long, id: Long)

    @Transaction
    suspend fun toggleFavourite(anime: AnimeEntity) {
        val exists = isFavouriteSyncInternal(anime.userId, anime.id)
        if (exists) {
            removeFromFavourites(anime.userId, anime.id)
        } else {
            addToFavourites(anime)
        }
    }

    @Query("SELECT EXISTS(SELECT 1 FROM favourites WHERE userId = :userId AND id = :id)")
    suspend fun isFavouriteSyncInternal(userId: Long, id: Long): Boolean


    // --- профили пользователей (users) ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId: Long)

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Long): UserEntity?


    // --- история просмотров (recent) ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecent(recent: RecentAnimeEntity)

    // получаем историю просмотров вместе с деталями аниме из кэша
    @Query("SELECT c.* FROM recent_anime r JOIN cached_anime_details c ON r.animeId = c.animeId WHERE r.userId = :userId ORDER BY r.viewedAt DESC")
    fun getRecentAnimeDetails(userId: Long): Flow<List<CachedAnimeDetailsEntity>>

    @Query("DELETE FROM recent_anime WHERE userId = :userId")
    suspend fun clearHistory(userId: Long)


    // --- личные заметки и оценки (notes) ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: AnimeNoteEntity)

    @Query("SELECT * FROM anime_notes WHERE userId = :userId AND animeId = :animeId")
    fun getNote(userId: Long, animeId: Long): Flow<AnimeNoteEntity?>

    @Query("SELECT * FROM anime_notes WHERE userId = :userId AND animeId = :animeId")
    suspend fun getNoteSync(userId: Long, animeId: Long): AnimeNoteEntity?

    @Query("DELETE FROM anime_notes WHERE userId = :userId AND animeId = :animeId")
    suspend fun deleteNote(userId: Long, animeId: Long)

    @Query("SELECT * FROM anime_notes WHERE userId = :userId")
    fun getAllNotes(userId: Long): Flow<List<AnimeNoteEntity>>


    // --- оффлайн-кэш деталей (cached details) ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedDetails(details: CachedAnimeDetailsEntity)

    @Query("SELECT * FROM cached_anime_details WHERE animeId = :animeId")
    suspend fun getCachedDetails(animeId: Long): CachedAnimeDetailsEntity?

    @Query("""
        DELETE FROM cached_anime_details
        WHERE cachedAt < :maxAge
          AND animeId NOT IN (SELECT id FROM favourites)
          AND animeId NOT IN (SELECT animeId FROM recent_anime)
    """)
    suspend fun deleteOldCache(maxAge: Long)

    @Query("SELECT animeId FROM cached_anime_details")
    suspend fun getAllCachedAnimeIds(): List<Long>
}