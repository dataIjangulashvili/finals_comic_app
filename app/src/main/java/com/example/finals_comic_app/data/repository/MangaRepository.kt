package com.example.finals_comic_app.data.repository

import com.example.finals_comic_app.data.local.MangaDao
import com.example.finals_comic_app.data.local.MangaEntity
import com.example.finals_comic_app.data.model.MangaResponse
import com.example.finals_comic_app.data.remote.JikanApi
import com.example.finals_comic_app.data.remote.MangaDetailsResponse
import com.example.finals_comic_app.data.remote.RetrofitInstance
import com.example.finals_comic_app.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class MangaRepository(private val mangaDao: MangaDao) {
    private val api = RetrofitInstance.api

    suspend fun getTopManga(page: Int = 1): Resource<MangaResponse> = withContext(Dispatchers.IO) {
        try {
            Resource.Success(api.getTopManga(page))
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    suspend fun searchManga(query: String, page: Int = 1): Resource<MangaResponse> = withContext(Dispatchers.IO) {
        try {
            Resource.Success(api.searchManga(query, page))
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    suspend fun getMangaDetails(id: Int): Resource<MangaDetailsResponse> = withContext(Dispatchers.IO) {
        try {
            Resource.Success(api.getMangaDetails(id))
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    // Room operations
    fun getAllFollowing(): Flow<List<MangaEntity>> = mangaDao.getAllFollowing()

    suspend fun insertFollowing(manga: MangaEntity) = withContext(Dispatchers.IO) {
        mangaDao.insertFollowing(manga)
    }

    suspend fun deleteFollowing(manga: MangaEntity) = withContext(Dispatchers.IO) {
        mangaDao.deleteFollowing(manga)
    }

    suspend fun isFollowing(id: Int): Boolean = withContext(Dispatchers.IO) {
        mangaDao.isFollowing(id)
    }

    suspend fun updateFollowing(manga: MangaEntity) = withContext(Dispatchers.IO) {
        mangaDao.updateFollowing(manga)
    }
}
