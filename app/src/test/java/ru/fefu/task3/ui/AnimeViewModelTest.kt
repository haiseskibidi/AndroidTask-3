package ru.fefu.task3.ui

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ru.fefu.task3.domain.model.AnimeRepository
import ru.fefu.task3.data.settings.UserSettings
import ru.fefu.task3.domain.model.AnimeBase
import ru.fefu.task3.util.MainDispatcherRule

/**
 * тесты для AnimeViewModel.
 * проверяем логику переключения стейтов, обработку ошибок и реактивные потоки.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AnimeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: AnimeRepository
    private lateinit var userSettings: UserSettings
    private lateinit var viewModel: AnimeViewModel

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        userSettings = mockk(relaxed = true)
        // по умолчанию репозиторий возвращает пустой список
        coEvery { repository.getAnimes(any()) } returns emptyList()
        every { repository.getFavouriteAnimes(any()) } returns flowOf(emptyList())
        every { repository.getRecentAnimeDetails(any()) } returns flowOf(emptyList())
        every { repository.getAllNotes(any()) } returns flowOf(emptyList())
        every { repository.getAllUsers() } returns flowOf(listOf(ru.fefu.task3.domain.model.User(id = 1L, name = "Test User")))
        every { userSettings.activeUserId } returns flowOf(1L)
        every { userSettings.isDarkTheme } returns flowOf(false)
        viewModel = AnimeViewModel(repository, userSettings)
    }

    @Test
    fun `initial state should be Loading`() = runTest {
        // проверяем только начальное значение без запуска потока
        assertThat(viewModel.listUiState.value).isInstanceOf(ListUiState.Loading::class.java)
    }

    @Test
    fun `successful search should emit Success state`() = runTest {
        val mockAnimes = listOf(
            AnimeBase(1, "Naruto", null, null, null, null, null)
        )
        coEvery { repository.getAnimes("naruto") } returns mockAnimes

        viewModel.onListEvent(ListEvent.SearchQueryChanged("naruto"))

        // используем turbine для проверки последовательности эмиссий
        viewModel.listUiState.test {
            // первое состояние может быть Loading или Success в зависимости от скорости выполнения
            val first = awaitItem()
            if (first is ListUiState.Loading) {
                assertThat(awaitItem()).isEqualTo(ListUiState.Success(mockAnimes))
            } else {
                assertThat(first).isEqualTo(ListUiState.Success(mockAnimes))
            }
        }
    }

    @Test
    fun `empty search result should emit Empty state`() = runTest {
        coEvery { repository.getAnimes("invalid") } returns emptyList()

        viewModel.onListEvent(ListEvent.SearchQueryChanged("invalid"))

        viewModel.listUiState.test {
            val item = awaitItem()
            if (item is ListUiState.Loading) {
                assertThat(awaitItem()).isEqualTo(ListUiState.Empty)
            } else {
                assertThat(item).isEqualTo(ListUiState.Empty)
            }
        }
    }

    @Test
    fun `network error should emit Error state`() = runTest {
        coEvery { repository.getAnimes(any()) } throws Exception("Network error")

        viewModel.onListEvent(ListEvent.SearchQueryChanged("error"))

        viewModel.listUiState.test {
            val item = awaitItem()
            if (item is ListUiState.Loading) {
                assertThat(awaitItem()).isInstanceOf(ListUiState.Error::class.java)
            } else {
                assertThat(item).isInstanceOf(ListUiState.Error::class.java)
            }
        }
    }

    @Test
    fun `retry should re-trigger repository call`() = runTest {
        coEvery { repository.getAnimes(any()) } returns emptyList()

        // запускаем сборку flow, чтобы блок flatMapLatest начал работать
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.listUiState.collect {}
        }

        viewModel.onListEvent(ListEvent.Retry)

        // проверяем, что метод репозитория был вызван (минимум 2 раза: при старте и при retry)
        coVerify(atLeast = 2) { repository.getAnimes(any()) }
    }

    @Test
    fun `rapid query changes should cancel previous requests - non-trivial flow behavior`() = runTest {
        val mockAnimes1 = listOf(AnimeBase(1, "First", null, null, null, null, null))
        val mockAnimes2 = listOf(AnimeBase(2, "Second", null, null, null, null, null))
        
        // имитируем задержку первого запроса
        coEvery { repository.getAnimes("first") } coAnswers {
            kotlinx.coroutines.delay(1000)
            mockAnimes1
        }
        coEvery { repository.getAnimes("second") } returns mockAnimes2

        viewModel.listUiState.test {
            // ждем завершения начальной инициализации
            advanceUntilIdle()
            
            // очищаем очередь событий Turbine, чтобы сфокусироваться на тесте
            while (true) {
                val item = expectMostRecentItem()
                if (item is ListUiState.Empty || item is ListUiState.Success) break
            }

            // вводим первый запрос
            viewModel.onListEvent(ListEvent.SearchQueryChanged("first"))
            advanceTimeBy(600) // ждем debounce
            
            // началось выполнение первого запроса
            assertThat(awaitItem()).isInstanceOf(ListUiState.Loading::class.java)

            // вводим второй запрос через 200мс (пока первый еще в delay)
            advanceTimeBy(200)
            viewModel.onListEvent(ListEvent.SearchQueryChanged("second"))
            advanceTimeBy(600) // ждем debounce для второго

            // первый запрос должен быть отменен, начинается второй
            // мы ожидаем Loading для второго запроса (т.к. flatMapLatest перезапустил flow)
            // и затем Success для второго запроса
            val next = awaitItem()
            if (next is ListUiState.Loading) {
                assertThat(awaitItem()).isEqualTo(ListUiState.Success(mockAnimes2))
            } else {
                assertThat(next).isEqualTo(ListUiState.Success(mockAnimes2))
            }
            
            // Success от первого ("first") не должен прийти
            expectNoEvents()
        }
    }

    @Test
    fun `selectUser should update activeUserId flow`() = runTest {
        advanceUntilIdle()
        viewModel.selectUser(42L)
        coVerify { userSettings.setActiveUserId(42L) }
    }

    @Test
    fun `toggleTheme should update isDarkTheme flow`() = runTest {
        advanceUntilIdle()
        viewModel.toggleTheme(true)
        coVerify { userSettings.setDarkTheme(true) }
    }

    @Test
    fun `clearHistory should trigger repository clean history`() = runTest {
        advanceUntilIdle()
        viewModel.clearHistory()
        coVerify { repository.clearHistory(1L) }
    }
}
