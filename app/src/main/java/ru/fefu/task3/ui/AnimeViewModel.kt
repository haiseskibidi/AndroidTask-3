package ru.fefu.task3.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.fefu.task3.data.model.AnimeBase
import ru.fefu.task3.data.model.AnimeDetails
import ru.fefu.task3.data.repository.AnimeRepository
import ru.fefu.task3.data.toAnimeBase
import ru.fefu.task3.data.toEntity
import javax.inject.Inject

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

sealed interface ListEvent {
    data class SearchQueryChanged(val query: String) : ListEvent
    object Retry : ListEvent
}

@HiltViewModel
class AnimeViewModel @Inject constructor(
    private val repository: AnimeRepository
) : ViewModel() {

    private val _listUiState = MutableStateFlow<ListUiState>(ListUiState.Loading)
    val listUiState: StateFlow<ListUiState> = _listUiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _detailUiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val detailUiState: StateFlow<DetailUiState> = _detailUiState.asStateFlow()

    val favouritesList: StateFlow<List<AnimeBase>> = repository.getFavouriteAnimes()
        .map { list -> list.map { it.toAnimeBase() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var searchJob: Job? = null
    private var detailJob: Job? = null
    private var favouriteJob: Job? = null

    init {
        loadAnimes()
    }

    fun onListEvent(event: ListEvent) {
        when (event) {
            is ListEvent.SearchQueryChanged -> {
                _searchQuery.value = event.query
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    delay(500)
                    if (event.query.isBlank()) {
                        loadAnimes(null)
                    } else {
                        loadAnimes(event.query)
                    }
                }
            }
            ListEvent.Retry -> {
                loadAnimes(_searchQuery.value.ifBlank { null })
            }
        }
    }

    private fun loadAnimes(query: String? = null) {
        _listUiState.value = ListUiState.Loading
        viewModelScope.launch {
            try {
                val result = repository.getAnimes(query)
                if (result.isEmpty()) {
                    _listUiState.value = ListUiState.Empty
                } else {
                    _listUiState.value = ListUiState.Success(result)
                }
            } catch (e: Exception) {
                _listUiState.value = ListUiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun loadAnimeDetails(id: Long) {
        detailJob?.cancel()
        favouriteJob?.cancel()
        _detailUiState.value = DetailUiState.Loading
        
        detailJob = viewModelScope.launch {
            try {
                val details = repository.getAnimeDetails(id)
                favouriteJob = launch {
                    repository.isFavourite(id).collect { isFav ->
                        val currentState = _detailUiState.value
                        if (currentState is DetailUiState.Success) {
                            _detailUiState.value = currentState.copy(isFavourite = isFav)
                        } else {
                            _detailUiState.value = DetailUiState.Success(details, isFav)
                        }
                    }
                }
            } catch (e: Exception) {
                _detailUiState.value = DetailUiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun toggleFavourite(animeDetails: AnimeDetails) {
        viewModelScope.launch {
            val isFav = repository.isFavouriteSync(animeDetails.id)
            if (isFav) {
                repository.removeFromFavourites(animeDetails.id)
            } else {
                repository.addToFavourites(animeDetails.toEntity())
            }
        }
    }
}
