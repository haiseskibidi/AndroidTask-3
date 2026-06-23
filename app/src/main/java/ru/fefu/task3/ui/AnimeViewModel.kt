package ru.fefu.task3.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.fefu.task3.domain.model.AnimeBase
import ru.fefu.task3.domain.model.AnimeDetails
import ru.fefu.task3.data.repository.AnimeRepository
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

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _retryTrigger = MutableSharedFlow<Unit>(replay = 0)

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val listUiState: StateFlow<ListUiState> = combine(
        _searchQuery
            .debounce { if (it.isEmpty()) 0L else 500L }
            .distinctUntilChanged(),
        _retryTrigger.onStart { emit(Unit) }
    ) { query, _ -> query }
        .flatMapLatest { query ->
            flow {
                emit(ListUiState.Loading)
                try {
                    val result = repository.getAnimes(query.ifBlank { null })
                    if (result.isEmpty()) {
                        emit(ListUiState.Empty)
                    } else {
                        emit(ListUiState.Success(result))
                    }
                } catch (e: Exception) {
                    emit(ListUiState.Error(e.localizedMessage ?: "Unknown error"))
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ListUiState.Loading)

    private val _detailAnimeId = MutableStateFlow<Long?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val detailUiState: StateFlow<DetailUiState> = _detailAnimeId
        .filterNotNull()
        .flatMapLatest { id ->
            flow {
                emit(DetailUiState.Loading)
                try {
                    val details = repository.getAnimeDetails(id)
                    emitAll(
                        repository.isFavourite(id).map { isFav ->
                            DetailUiState.Success(details, isFav)
                        }
                    )
                } catch (e: Exception) {
                    emit(DetailUiState.Error(e.localizedMessage ?: "Unknown error"))
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DetailUiState.Loading)

    val favouritesList: StateFlow<List<AnimeBase>> = repository.getFavouriteAnimes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onListEvent(event: ListEvent) {
        when (event) {
            is ListEvent.SearchQueryChanged -> {
                _searchQuery.value = event.query
            }
            ListEvent.Retry -> {
                viewModelScope.launch {
                    _retryTrigger.emit(Unit)
                }
            }
        }
    }

    fun loadAnimeDetails(id: Long) {
        _detailAnimeId.value = id
    }

    fun toggleFavourite(animeDetails: AnimeDetails) {
        viewModelScope.launch {
            repository.toggleFavourite(animeDetails)
        }
    }
}
