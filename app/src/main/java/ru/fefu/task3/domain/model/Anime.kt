package ru.fefu.task3.domain.model

import kotlinx.coroutines.flow.Flow

interface AnimeRepository {
    suspend fun getAnimes(search: String? = null): List<AnimeBase>
    suspend fun getAnimeDetails(id: Long): AnimeDetails
    fun isFavourite(userId: Long, id: Long): Flow<Boolean>
    fun getFavouriteAnimes(userId: Long): Flow<List<AnimeBase>>
    suspend fun toggleFavourite(userId: Long, anime: AnimeDetails)
    fun getAllUsers(): Flow<List<User>>
    suspend fun insertUser(user: User): Long
    suspend fun deleteUser(userId: Long)
    suspend fun getUserById(userId: Long): User?
    suspend fun getOrCreateDefaultUser(): Long
    suspend fun addToRecent(userId: Long, animeId: Long)
    fun getRecentAnimeDetails(userId: Long): Flow<List<AnimeBase>>
    suspend fun clearHistory(userId: Long)
    fun getNote(userId: Long, animeId: Long): Flow<AnimeNote?>
    fun getAllNotes(userId: Long): Flow<List<AnimeNote>>
    suspend fun saveNote(note: AnimeNote)
    suspend fun deleteNote(userId: Long, animeId: Long)
    suspend fun deleteOldCache(maxAge: Long)
    suspend fun getAllCachedAnimeIds(): List<Long>
    suspend fun forceCacheDetails(id: Long)
}

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
    val isFavourite: Boolean = false,
    val userRating: Float? = null
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

data class User(
    val id: Long = 0,
    val name: String,
    val avatarUrl: String? = null
)

data class AnimeNote(
    val id: Long = 0,
    val userId: Long,
    val animeId: Long,
    val noteText: String,
    val rating: Float? = null,
    val updatedAt: Long
)