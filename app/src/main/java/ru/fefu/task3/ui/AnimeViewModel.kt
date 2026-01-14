package ru.fefu.task3.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.fefu.task3.data.model.AnimeBase
import ru.fefu.task3.data.model.AnimeDetails
import ru.fefu.task3.data.repository.AnimeRepository
import ru.fefu.task3.util.TrigramUtil

sealed class ListUiState {
    object Loading : ListUiState()
    data class Success(val animes: List<AnimeBase>) : ListUiState()
    data class Error(val message: String) : ListUiState()
    object Empty : ListUiState()
}

sealed class DetailUiState {
    object Loading : DetailUiState()
    data class Success(val anime: AnimeDetails, val isFavourite: Boolean) : DetailUiState()
    data class Error(val message: String) : DetailUiState()
}

class AnimeViewModel : ViewModel() {
    private val repository = AnimeRepository()

    private val _listUiState = MutableStateFlow<ListUiState>(ListUiState.Loading)
    val listUiState: StateFlow<ListUiState> = _listUiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _detailUiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val detailUiState: StateFlow<DetailUiState> = _detailUiState.asStateFlow()

    private val _favouritesList = MutableStateFlow<List<AnimeBase>>(emptyList())
    val favouritesList: StateFlow<List<AnimeBase>> = _favouritesList.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadAnimes()
        refreshFavourites()
    }

    fun loadAnimes(query: String? = null) {
        _listUiState.value = ListUiState.Loading
        viewModelScope.launch {
            try {
                val result = repository.getAnimes(query)
                val sortedResult = if (!query.isNullOrBlank()) {
                    result.sortedByDescending { anime ->
                        val simName = TrigramUtil.calculateSimilarity(query, anime.name)
                        val simRus = TrigramUtil.calculateSimilarity(query, anime.russian)
                        maxOf(simName, simRus)
                    }
                } else result

                if (sortedResult.isEmpty()) _listUiState.value = ListUiState.Empty
                else _listUiState.value = ListUiState.Success(sortedResult)
            } catch (e: Exception) {
                _listUiState.value = ListUiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500)
            if (newQuery.isBlank()) loadAnimes(null)
            else loadAnimes(newQuery)
        }
    }

    fun loadAnimeDetails(id: Long) {
        _detailUiState.value = DetailUiState.Loading
        viewModelScope.launch {
            try {
                val details = repository.getAnimeDetails(id)
                val isFav = repository.isFavourite(id)
                _detailUiState.value = DetailUiState.Success(details, isFav)
            } catch (e: Exception) {
                _detailUiState.value = DetailUiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun toggleFavourite(animeDetails: AnimeDetails) {
        val base = AnimeBase(
            id = animeDetails.id,
            name = animeDetails.name,
            russian = animeDetails.russian,
            image = animeDetails.image,
            score = animeDetails.score,
            kind = animeDetails.kind,
            status = animeDetails.status
        )
        
        if (repository.isFavourite(animeDetails.id)) repository.removeFromFavourites(animeDetails.id)
        else repository.addToFavourites(base)
        
        val current = _detailUiState.value
        if (current is DetailUiState.Success) {
            _detailUiState.value = current.copy(isFavourite = repository.isFavourite(animeDetails.id))
        }
        refreshFavourites()
    }

    private fun refreshFavourites() {
        _favouritesList.value = repository.getFavouriteAnimes()
    }
}