package com.tushar.wallpapers.repository


import androidx.lifecycle.LiveData
import com.tushar.wallpapers.api.RetrofitInstance
import com.tushar.wallpapers.model.Photo
import com.tushar.wallpapers.model.PhotoDao

class WallpaperRepository(private val photoDao: PhotoDao) {
    val allFavorites: LiveData<List<Photo>> = photoDao.getAllFavorites()

    suspend fun addToFavorites(photo: Photo) {
        photoDao.addToFavorites(photo)
    }

    suspend fun removeFromFavorites(photo: Photo) {
        photoDao.removeFromFavorites(photo)
    }

    suspend fun isFavorite(id: Int): Boolean {
        return photoDao.isFavorite(id)
    }
    suspend fun getWallpapersByQuery(query: String): List<Photo>? {
        return try {
            val response = RetrofitInstance.api.searchWallpapers(query = query, perPage = 10)
            response.photos
        } catch (e: Exception) {
            null
        }
    }
}
