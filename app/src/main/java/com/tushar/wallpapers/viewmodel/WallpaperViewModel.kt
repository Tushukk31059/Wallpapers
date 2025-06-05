package com.tushar.wallpapers.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

import androidx.lifecycle.viewModelScope

import com.tushar.wallpapers.api.RetrofitInstance
import com.tushar.wallpapers.model.Category
import com.tushar.wallpapers.model.Photo
import com.tushar.wallpapers.model.WallpaperDatabase
import com.tushar.wallpapers.repository.WallpaperRepository
import kotlinx.coroutines.launch

class WallpaperViewModel(application: Application) : AndroidViewModel(application){

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> get() = _categories

    private val _wallpapers = MutableLiveData<List<Photo>>()
    val wallpapers: LiveData<List<Photo>> get() = _wallpapers

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val repository: WallpaperRepository
    val allFavorites: LiveData<List<Photo>>

    init {
        val photoDao = WallpaperDatabase.getDatabase(application).photoDao()
        repository = WallpaperRepository(photoDao)
        allFavorites = repository.allFavorites
    }

    fun loadCategories() {
        _categories.value = listOf(
            Category("Nature", "https://images.pexels.com/photos/34950/pexels-photo.jpg"),
            Category("Cars", "https://images.pexels.com/photos/210019/pexels-photo-210019.jpeg"),
            Category("Animals", "https://images.pexels.com/photos/145939/pexels-photo-145939.jpeg"),
            Category("Abstract", "https://images.pexels.com/photos/370799/pexels-photo-370799.jpeg"),
            Category("City", "https://images.pexels.com/photos/374870/pexels-photo-374870.jpeg"),
            Category("Mountains", "https://images.pexels.com/photos/552785/pexels-photo-552785.jpeg")
        )
    }

    fun fetchWallpapers(query: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.searchWallpapers(query, 30)
                _wallpapers.value = response.photos
                _errorMessage.value = ""
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load wallpapers: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    // Room: Add to Favorites
    fun addToFavorites(photo: Photo) = viewModelScope.launch {
        repository.addToFavorites(photo)
    }

    // Room: Remove from Favorites
    fun removeFromFavorites(photo: Photo) = viewModelScope.launch {
        repository.removeFromFavorites(photo)
    }
    // Room: Check if already favorite
    fun isFavorite(id: Int, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.isFavorite(id)
            callback(result)
        }
    }
}
