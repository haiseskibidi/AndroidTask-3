package ru.fefu.task3.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ru.fefu.task3.data.db.AnimeDao
import ru.fefu.task3.data.db.AnimeNoteEntity
import ru.fefu.task3.data.db.RecentAnimeEntity
import ru.fefu.task3.data.db.UserEntity
import ru.fefu.task3.data.toAnimeBase
import ru.fefu.task3.data.toCachedEntity
import ru.fefu.task3.data.toDomain
import ru.fefu.task3.data.toEntity
import ru.fefu.task3.domain.model.AnimeBase
import ru.fefu.task3.domain.model.AnimeDetails
import ru.fefu.task3.domain.model.User
import ru.fefu.task3.domain.model.AnimeNote
import ru.fefu.task3.data.network.ShikimoriApi
import ru.fefu.task3.data.network.dto.toDomain
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnimeRepositoryImpl @Inject constructor(
    private val api: ShikimoriApi,
    private val dao: AnimeDao
) : ru.fefu.task3.domain.model.AnimeRepository {

    override suspend fun getAnimes(search: String?): List<AnimeBase> =
        api.getAnimes(search = search).map { it.toDomain() }

    // оффлайн-первый подход с кэшированием в Room
    override suspend fun getAnimeDetails(id: Long): AnimeDetails {
        return try {
            val networkDetails = api.getAnimeDetails(id).toDomain()
            dao.insertCachedDetails(networkDetails.toCachedEntity())
            networkDetails
        } catch (e: Exception) {
            val cached = dao.getCachedDetails(id) ?: throw e
            cached.toDomain()
        }
    }

    // --- избранное (пользовательские данные) ---
    override fun isFavourite(userId: Long, id: Long): Flow<Boolean> = dao.isFavourite(userId, id)
    
    override fun getFavouriteAnimes(userId: Long): Flow<List<AnimeBase>> = dao.getAllFavourites(userId)
        .map { list -> list.map { it.toAnimeBase() } }
    
    override suspend fun toggleFavourite(userId: Long, anime: AnimeDetails) {
        dao.toggleFavourite(anime.toEntity(userId))
    }

    // --- профили пользователей ---
    override fun getAllUsers(): Flow<List<User>> = dao.getAllUsers()
        .map { list -> list.map { it.toDomain() } }
    
    override suspend fun insertUser(user: User): Long = dao.insertUser(user.toEntity())
    
    override suspend fun deleteUser(userId: Long) = dao.deleteUser(userId)

    override suspend fun getUserById(userId: Long): User? = dao.getUserById(userId)?.toDomain()

    override suspend fun getOrCreateDefaultUser(): Long {
        val users = dao.getAllUsers().first()
        return if (users.isEmpty()) {
            dao.insertUser(UserEntity(name = "Главный профиль"))
        } else {
            users.first().id
        }
    }

    // --- история просмотров (recent) ---
    override suspend fun addToRecent(userId: Long, animeId: Long) {
        dao.insertRecent(
            RecentAnimeEntity(
                userId = userId,
                animeId = animeId,
                viewedAt = System.currentTimeMillis()
            )
        )
    }

    override fun getRecentAnimeDetails(userId: Long): Flow<List<AnimeBase>> = dao.getRecentAnimeDetails(userId)
        .map { list -> list.map { it.toAnimeBase() } }

    override suspend fun clearHistory(userId: Long) = dao.clearHistory(userId)

    // --- заметки ---
    override fun getNote(userId: Long, animeId: Long): Flow<AnimeNote?> = dao.getNote(userId, animeId)
        .map { it?.toDomain() }

    override fun getAllNotes(userId: Long): Flow<List<AnimeNote>> = dao.getAllNotes(userId)
        .map { list -> list.map { it.toDomain() } }

    override suspend fun saveNote(note: AnimeNote) = dao.insertNote(note.toEntity())

    override suspend fun deleteNote(userId: Long, animeId: Long) = dao.deleteNote(userId, animeId)

    // --- управление кэшем для воркера ---
    override suspend fun deleteOldCache(maxAge: Long) = dao.deleteOldCache(maxAge)

    override suspend fun getAllCachedAnimeIds(): List<Long> = dao.getAllCachedAnimeIds()

    override suspend fun forceCacheDetails(id: Long) {
        val details = api.getAnimeDetails(id).toDomain()
        dao.insertCachedDetails(details.toCachedEntity())
    }
}
