package ru.fefu.task3.ui

import ru.fefu.task3.domain.model.AnimeBase
import ru.fefu.task3.domain.model.AnimeDetails

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
