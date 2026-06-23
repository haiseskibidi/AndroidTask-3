package ru.fefu.task3.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import ru.fefu.task3.data.db.AnimeDao
import ru.fefu.task3.data.db.AppDatabase
import ru.fefu.task3.data.network.ShikimoriApi
import ru.fefu.task3.domain.model.AnimeDetails

/**
 * интеграционный тест для связи Repository + Room.
 * проверяем, что репозиторий корректно делегирует задачи в БД.
 */
@RunWith(AndroidJUnit4::class)
class RepositoryIntegrationTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: AnimeDao
    private lateinit var repository: AnimeRepository
    private val api = object : ShikimoriApi {
        override suspend fun getAnimes(
            page: Int,
            limit: Int,
            search: String?,
            order: String
        ): List<ru.fefu.task3.data.network.dto.AnimeDto> = emptyList()

        override suspend fun getAnimeDetails(
            id: Long
        ): ru.fefu.task3.data.network.dto.AnimeDetailsDto {
            throw NotImplementedError()
        }
    }

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        dao = db.animeDao()
        repository = AnimeRepository(api, dao)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun toggleFavourite_actuallySavesToRoom() = runTest {
        val anime = AnimeDetails(
            id = 123,
            name = "Test",
            russian = "Тест",
            imageUrl = "url",
            score = "8.0",
            kind = "tv",
            status = "released",
            description = null,
            descriptionHtml = null,
            episodes = null,
            airedOn = null,
            genres = null
        )

        // выполняем сохранение через репозиторий
        repository.toggleFavourite(anime)

        // проверяем, что данные появились в БД
        val favourites = repository.getFavouriteAnimes().first()
        assertThat(favourites).hasSize(1)
        assertThat(favourites.first().id).isEqualTo(123)
        assertThat(favourites.first().name).isEqualTo("Test")
    }

    @Test
    fun toggleFavourite_removesFromRoomIfExisted() = runTest {
        val anime = AnimeDetails(
            id = 123,
            name = "Test",
            russian = "Тест",
            imageUrl = "url",
            score = "8.0",
            kind = "tv",
            status = "released",
            description = null,
            descriptionHtml = null,
            episodes = null,
            airedOn = null,
            genres = null
        )

        // добавляем
        repository.toggleFavourite(anime)
        // удаляем (повторный клик)
        repository.toggleFavourite(anime)

        val favourites = repository.getFavouriteAnimes().first()
        assertThat(favourites).isEmpty()
    }
}
