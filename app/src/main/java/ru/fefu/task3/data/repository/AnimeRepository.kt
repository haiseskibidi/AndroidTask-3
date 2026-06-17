package ru.fefu.task3.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import ru.fefu.task3.data.db.AnimeDao
import ru.fefu.task3.data.db.AnimeEntity
import ru.fefu.task3.data.model.AnimeBase
import ru.fefu.task3.data.model.AnimeDetails
import ru.fefu.task3.data.network.ShikimoriApi
import ru.fefu.task3.data.network.dto.toDomain
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnimeRepository @Inject constructor(
    private val api: ShikimoriApi,
    private val dao: AnimeDao
) {

    suspend fun getAnimes(search: String? = null): List<AnimeBase> = withContext(Dispatchers.IO) {
        api.getAnimes(search = search).map { it.toDomain() }
    }

    suspend fun getAnimeDetails(id: Long): AnimeDetails = withContext(Dispatchers.IO) {
        api.getAnimeDetails(id).toDomain()
    }

    fun isFavourite(id: Long): Flow<Boolean> = dao.isFavourite(id)
    
    suspend fun isFavouriteSync(id: Long): Boolean = withContext(Dispatchers.IO) {
        dao.isFavouriteSync(id)
    }

    fun getFavouriteAnimes(): Flow<List<AnimeEntity>> = dao.getAllFavourites()
    
    suspend fun addToFavourites(anime: AnimeEntity) = withContext(Dispatchers.IO) {
        dao.addToFavourites(anime)
    }
    
    suspend fun removeFromFavourites(animeId: Long) = withContext(Dispatchers.IO) {
        dao.removeFromFavourites(animeId)
    }

    suspend fun toggleFavourite(entity: AnimeEntity) = withContext(Dispatchers.IO) {
        dao.toggleFavourite(entity)
    }
}
