package com.tushar.wallpapers.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tushar.wallpapers.api.ApiUtilities
import com.tushar.wallpapers.api.RetrofitInstance
import com.tushar.wallpapers.model.Category
import com.tushar.wallpapers.model.Photo
import kotlinx.coroutines.launch

class WallpaperViewModel : ViewModel() {

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> get() = _categories

    private val _wallpapers = MutableLiveData<List<Photo>>()
    val wallpapers: LiveData<List<Photo>> get() = _wallpapers

    init {
        loadCategories()
        fetchWallpapers("Nature") // Load default wallpapers
    }

    private fun loadCategories() {
        val dummyCategories = listOf(
            Category("Nature", "https://images.pexels.com/photos/34950/pexels-photo.jpg"),
            Category("Cars", "https://images.pexels.com/photos/210019/pexels-photo-210019.jpeg"),
            Category("Animals", "https://images.pexels.com/photos/145939/pexels-photo-145939.jpeg"),
            Category("Abstract", "https://images.pexels.com/photos/370799/pexels-photo-370799.jpeg"),
            Category("City", "https://images.pexels.com/photos/374870/pexels-photo-374870.jpeg"),
            Category("Mountains", "https://images.pexels.com/photos/552785/pexels-photo-552785.jpeg")
        )

        _categories.value = dummyCategories
    }

    fun fetchWallpapers(query: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.searchWallpapers(query, 30)
                _wallpapers.value = response.photos
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
