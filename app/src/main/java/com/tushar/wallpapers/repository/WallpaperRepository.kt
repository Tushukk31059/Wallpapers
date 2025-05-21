package com.tushar.wallpapers.repository


import com.tushar.wallpapers.api.RetrofitInstance
import com.tushar.wallpapers.model.Photo

class WallpaperRepository {
    suspend fun getWallpapersByQuery(query: String): List<Photo>? {
        return try {
            val response = RetrofitInstance.api.searchWallpapers(query = query, perPage = 10)
            response.photos
        } catch (e: Exception) {
            null
        }
    }
}
