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
import ru.fefu.task3.domain.model.AnimeRepository
import ru.fefu.task3.domain.model.User
import ru.fefu.task3.domain.model.AnimeNote
import ru.fefu.task3.data.settings.UserSettings
import javax.inject.Inject


@HiltViewModel
class AnimeViewModel @Inject constructor(
    private val repository: AnimeRepository,
    private val userSettings: UserSettings
) : ViewModel() {

    val activeUserId: StateFlow<Long?> = userSettings.activeUserId
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val isDarkTheme: StateFlow<Boolean?> = userSettings.isDarkTheme
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val allUsers: StateFlow<List<User>> = repository.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val favouritesList: StateFlow<List<AnimeBase>> = activeUserId
        .filterNotNull()
        .flatMapLatest { userId ->
            combine(
                repository.getFavouriteAnimes(userId),
                repository.getAllNotes(userId)
            ) { favourites, notes ->
                val notesMap = notes.associateBy { it.animeId }
                favourites.map { it.copy(userRating = notesMap[it.id]?.rating) }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val recentAnimes: StateFlow<List<AnimeBase>> = activeUserId
        .filterNotNull()
        .flatMapLatest { userId ->
            combine(
                repository.getRecentAnimeDetails(userId),
                repository.getFavouriteAnimes(userId),
                repository.getAllNotes(userId)
            ) { recent, favourites, notes ->
                val favIds = favourites.map { it.id }.toSet()
                val notesMap = notes.associateBy { it.animeId }
                recent.map { it.copy(isFavourite = it.id in favIds, userRating = notesMap[it.id]?.rating) }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _retryTrigger = MutableSharedFlow<Unit>(replay = 0)

    init {
        viewModelScope.launch {
            val activeId = userSettings.activeUserId.first()
            if (activeId == null || repository.getUserById(activeId) == null) {
                userSettings.setActiveUserId(repository.getOrCreateDefaultUser())
            }
        }
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val listUiState: StateFlow<ListUiState> = combine(
        _searchQuery.debounce { if (it.isEmpty()) 0L else 500L }.distinctUntilChanged(),
        _retryTrigger.onStart { emit(Unit) },
        activeUserId.filterNotNull()
    ) { query, _, userId -> Pair(query, userId) }
        .flatMapLatest { (query, userId) ->
            flow {
                emit(ListUiState.Loading)
                try {
                    val networkList = repository.getAnimes(query.ifBlank { null })
                    if (networkList.isEmpty()) {
                        emit(ListUiState.Empty)
                    } else {
                        emitAll(
                            combine(
                                repository.getFavouriteAnimes(userId),
                                repository.getAllNotes(userId)
                            ) { favourites, notes ->
                                val favIds = favourites.map { it.id }.toSet()
                                val notesMap = notes.associateBy { it.animeId }
                                val updated = networkList.map {
                                    it.copy(isFavourite = it.id in favIds, userRating = notesMap[it.id]?.rating)
                                }
                                ListUiState.Success(updated)
                            }
                        )
                    }
                } catch (e: Exception) {
                    emit(ListUiState.Error(e.localizedMessage ?: "Unknown error"))
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ListUiState.Loading)

    private val _detailAnimeId = MutableStateFlow<Long?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val detailUiState: StateFlow<DetailUiState> = combine(
        _detailAnimeId,
        activeUserId.filterNotNull()
    ) { animeId, userId -> Pair(animeId, userId) }
        .flatMapLatest { (animeId, userId) ->
            flow {
                emit(DetailUiState.Loading)
                if (animeId != null) {
                    try {
                        val details = repository.getAnimeDetails(animeId)
                        repository.addToRecent(userId, animeId)
                        emitAll(
                            repository.isFavourite(userId, animeId).map { isFav ->
                                DetailUiState.Success(details, isFav)
                            }
                        )
                    } catch (e: Exception) {
                        emit(DetailUiState.Error(e.localizedMessage ?: "Unknown error"))
                    }
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DetailUiState.Loading)

    fun onListEvent(event: ListEvent) {
        when (event) {
            is ListEvent.SearchQueryChanged -> _searchQuery.value = event.query
            ListEvent.Retry -> viewModelScope.launch { _retryTrigger.emit(Unit) }
        }
    }

    fun loadAnimeDetails(id: Long) {
        if (_detailAnimeId.value != id) {
            _detailAnimeId.value = null
            viewModelScope.launch { _detailAnimeId.value = id }
        }
    }

    fun toggleFavourite(animeDetails: AnimeDetails) {
        activeUserId.value?.let { userId ->
            viewModelScope.launch { repository.toggleFavourite(userId, animeDetails) }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getNoteForAnime(animeId: Long): Flow<AnimeNote?> = activeUserId
        .flatMapLatest { userId ->
            if (userId == null) flowOf(null)
            else repository.getNote(userId, animeId)
        }

    fun saveNote(animeId: Long, text: String, rating: Float?) {
        activeUserId.value?.let { userId ->
            viewModelScope.launch {
                repository.saveNote(AnimeNote(userId = userId, animeId = animeId, noteText = text, rating = rating, updatedAt = System.currentTimeMillis()))
            }
        }
    }

    fun deleteNote(animeId: Long) {
        activeUserId.value?.let { viewModelScope.launch { repository.deleteNote(it, animeId) } }
    }

    fun createUser(name: String) {
        viewModelScope.launch { repository.insertUser(User(name = name)) }
    }

    fun selectUser(userId: Long) {
        viewModelScope.launch { userSettings.setActiveUserId(userId) }
    }

    fun deleteUser(userId: Long) {
        viewModelScope.launch { repository.deleteUser(userId) }
    }

    fun toggleTheme(enabled: Boolean) {
        viewModelScope.launch { userSettings.setDarkTheme(enabled) }
    }

    fun clearHistory() {
        activeUserId.value?.let { viewModelScope.launch { repository.clearHistory(it) } }
    }
}
