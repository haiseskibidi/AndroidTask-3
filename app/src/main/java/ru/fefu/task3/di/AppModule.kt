package ru.fefu.task3.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.fefu.task3.data.db.AnimeDao
import ru.fefu.task3.data.db.AppDatabase
import ru.fefu.task3.data.network.ShikimoriApi
import ru.fefu.task3.data.repository.AnimeRepositoryImpl
import ru.fefu.task3.domain.model.AnimeRepository
import ru.fefu.task3.BuildConfig
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        .addInterceptor { chain ->
            chain.proceed(
                chain.request().newBuilder()
                    .header("User-Agent", "Task3HomeworkApp/1.0")
                    .build()
            )
        }
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val baseUrl = BuildConfig.API_BASE_URL.ifBlank { "https://shikimori.one/api/" }
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideShikimoriApi(retrofit: Retrofit): ShikimoriApi {
        return retrofit.create(ShikimoriApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "shikimori_db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    fun provideAnimeDao(database: AppDatabase): AnimeDao {
        return database.animeDao()
    }

    @Provides
    @Singleton
    fun provideAnimeRepository(api: ShikimoriApi, dao: AnimeDao): AnimeRepository {
        return AnimeRepositoryImpl(api, dao)
    }
}
