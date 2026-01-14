package ru.fefu.task3.data.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.fefu.task3.data.model.AnimeBase
import ru.fefu.task3.data.model.AnimeDetails

interface ShikimoriApi {
    @GET("animes")
    suspend fun getAnimes(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("search") search: String? = null,
        @Query("order") order: String = "popularity"
    ): List<AnimeBase>

    @GET("animes/{id}")
    suspend fun getAnimeDetails(@Path("id") id: Long): AnimeDetails
}
