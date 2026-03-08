package ru.fefu.task3.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.fefu.task3.data.db.AnimeDao
import ru.fefu.task3.data.db.AppDatabase
import ru.fefu.task3.data.network.RetrofitClient
import ru.fefu.task3.data.network.ShikimoriApi
import ru.fefu.task3.data.repository.AnimeRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "shikimori_db"
        ).build()
    }

    @Provides
    fun provideAnimeDao(database: AppDatabase): AnimeDao {
        return database.animeDao()
    }

    @Provides
    @Singleton
    fun provideShikimoriApi(): ShikimoriApi {
        return RetrofitClient.api
    }

    @Provides
    @Singleton
    fun provideAnimeRepository(api: ShikimoriApi, dao: AnimeDao): AnimeRepository {
        return AnimeRepository(api, dao)
    }
}