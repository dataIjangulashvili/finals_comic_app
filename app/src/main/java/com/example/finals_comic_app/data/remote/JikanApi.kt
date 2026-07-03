package com.example.finals_comic_app.data.remote

import com.example.finals_comic_app.data.model.MangaResponse
import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface JikanApi {
    @GET("manga")
    suspend fun getTopManga(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("order_by") orderBy: String = "popularity"
    ): MangaResponse

    @GET("manga")
    suspend fun searchManga(
        @Query("q") query: String,
        @Query("page") page: Int = 1
    ): MangaResponse

    @GET("manga/{id}/full")
    suspend fun getMangaDetails(
        @Path("id") id: Int
    ): MangaDetailsResponse

    companion object {
        const val BASE_URL = "https://api.jikan.moe/v4/"
    }
}

data class MangaDetailsResponse(
    @SerializedName("data") val data: com.example.finals_comic_app.data.model.Manga
)
