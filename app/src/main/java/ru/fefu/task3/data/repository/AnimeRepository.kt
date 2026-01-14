package ru.fefu.task3.data.repository

import ru.fefu.task3.data.model.AnimeBase
import ru.fefu.task3.data.model.AnimeDetails
import ru.fefu.task3.data.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AnimeRepository {
    private val api = RetrofitClient.api
    private val _favourites = mutableSetOf<Long>()
    private val _favouriteObjects = mutableListOf<AnimeBase>()

    suspend fun getAnimes(search: String? = null): List<AnimeBase> = withContext(Dispatchers.IO) {
        api.getAnimes(search = search)
    }

    suspend fun getAnimeDetails(id: Long): AnimeDetails = withContext(Dispatchers.IO) {
        api.getAnimeDetails(id)
    }

    fun isFavourite(id: Long): Boolean = _favourites.contains(id)

    fun getFavouriteAnimes(): List<AnimeBase> = _favouriteObjects.toList()
    
    fun addToFavourites(anime: AnimeBase) {
        if (_favouriteObjects.none { it.id == anime.id }) {
            _favouriteObjects.add(anime)
            _favourites.add(anime.id)
        }
    }
    
    fun removeFromFavourites(animeId: Long) {
        _favouriteObjects.removeAll { it.id == animeId }
        _favourites.remove(animeId)
    }
}
