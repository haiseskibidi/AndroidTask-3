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
    private val _detailRetryTrigger = MutableSharedFlow<Unit>(replay = 0)

    private val _errorEvents = MutableSharedFlow<String>(replay = 0)
    val errorEvents = _errorEvents.asSharedFlow()

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
        _detailRetryTrigger.onStart { emit(Unit) },
        activeUserId.filterNotNull()
    ) { animeId, _, userId -> Pair(animeId, userId) }
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
        _detailAnimeId.value = id
    }

    fun retryDetail() {
        viewModelScope.launch { _detailRetryTrigger.emit(Unit) }
    }

    private fun launchWithDbError(action: suspend () -> Unit, errorMessage: String) {
        viewModelScope.launch {
            try {
                action()
            } catch (e: Exception) {
                _errorEvents.emit("$errorMessage: ${e.localizedMessage ?: "unknown error"}")
            }
        }
    }

    fun toggleFavourite(animeDetails: AnimeDetails) = activeUserId.value?.let { userId ->
        launchWithDbError({ repository.toggleFavourite(userId, animeDetails) }, "Ошибка БД")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getNoteForAnime(animeId: Long): Flow<AnimeNote?> = activeUserId
        .flatMapLatest { userId ->
            if (userId == null) flowOf(null)
            else repository.getNote(userId, animeId)
        }

    fun saveNote(animeId: Long, text: String, rating: Float?) = activeUserId.value?.let { userId ->
        launchWithDbError({
            repository.saveNote(AnimeNote(userId = userId, animeId = animeId, noteText = text, rating = rating, updatedAt = System.currentTimeMillis()))
        }, "Не удалось сохранить заметку")
    }

    fun deleteNote(animeId: Long) = activeUserId.value?.let { userId ->
        launchWithDbError({ repository.deleteNote(userId, animeId) }, "Не удалось удалить заметку")
    }

    fun createUser(name: String) = launchWithDbError({ repository.insertUser(User(name = name)) }, "Не удалось создать профиль")

    fun selectUser(userId: Long) = launchWithDbError({ userSettings.setActiveUserId(userId) }, "Не удалось переключить профиль")

    fun deleteUser(userId: Long) = launchWithDbError({ repository.deleteUser(userId) }, "Не удалось удалить профиль")

    fun toggleTheme(enabled: Boolean) = launchWithDbError({ userSettings.setDarkTheme(enabled) }, "Не удалось изменить тему")

    fun clearHistory() = activeUserId.value?.let { userId ->
        launchWithDbError({ repository.clearHistory(userId) }, "Не удалось очистить историю")
    }
}
