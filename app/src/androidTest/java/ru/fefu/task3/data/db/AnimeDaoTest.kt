package ru.fefu.task3.data.db

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
import java.io.IOException

/**
 * интеграционный тест для Room DAO.
 * проверяем корректность работы с базой данных в памяти.
 */
@RunWith(AndroidJUnit4::class)
class AnimeDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: AnimeDao
    private val userId = 1L

    @Before
    fun createDb() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // создаем базу данных в оперативной памяти для тестов
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        dao = db.animeDao()
        // вставляем пользователя для внешнего ключа
        dao.insertUser(UserEntity(id = userId, name = "Test User"))
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndReadFavourite() = runTest {
        val anime = AnimeEntity(
            id = 1,
            userId = userId,
            name = "Test Anime",
            russian = "Тест",
            imageUrl = "url",
            score = "9.0",
            kind = "tv",
            status = "released"
        )

        dao.addToFavourites(anime)
        
        val favourites = dao.getAllFavourites(userId).first()
        assertThat(favourites).contains(anime)
    }

    @Test
    fun deleteFavourite() = runTest {
        val anime = AnimeEntity(
            id = 1,
            userId = userId,
            name = "Test",
            russian = null,
            imageUrl = null,
            score = null,
            kind = null,
            status = null
        )
        dao.addToFavourites(anime)
        dao.removeFromFavourites(userId, 1)

        val favourites = dao.getAllFavourites(userId).first()
        assertThat(favourites).isEmpty()
    }

    @Test
    fun toggleFavourite_nonTrivial_preventsDuplicates() = runTest {
        val anime = AnimeEntity(
            id = 1,
            userId = userId,
            name = "Test",
            russian = null,
            imageUrl = null,
            score = null,
            kind = null,
            status = null
        )
        
        // добавляем дважды (второй раз через REPLACE)
        dao.addToFavourites(anime)
        dao.addToFavourites(anime)

        val favourites = dao.getAllFavourites(userId).first()
        // проверяем нетривиальный контракт: дубликатов быть не должно
        assertThat(favourites).hasSize(1)
    }
}
