package com.tushar.wallpapers

import android.os.Build
import android.os.Bundle
import androidx.lifecycle.Observer
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.tushar.wallpapers.adapter.PhotoAdapter

import com.tushar.wallpapers.databinding.ActivityFavouriteBinding
import com.tushar.wallpapers.model.Photo
import com.tushar.wallpapers.viewmodel.WallpaperViewModel

class FavouriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavouriteBinding
    private lateinit var viewModel: WallpaperViewModel
    private lateinit var photoAdapter: PhotoAdapter

    private val favoriteList = mutableListOf<Photo>()
    override fun onCreate(savedInstanceState: Bundle?) {

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        window.statusBarColor = ContextCompat.getColor(this, R.color.grey)

// Set status bar icons to dark (so they’re visible on white background)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR


        super.onCreate(savedInstanceState)
        binding = ActivityFavouriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[WallpaperViewModel::class.java]
        photoAdapter = PhotoAdapter(this, viewModel)

        binding.favoriteRecyclerView.layoutManager = GridLayoutManager(this@FavouriteActivity, 2)
        binding.favoriteRecyclerView.adapter = photoAdapter


        // Observe Favorites from Room DB
        viewModel.allFavorites.observe(this) { favorites ->
            if (favorites.isEmpty()) {
                Toast.makeText(this, "No favorites found", Toast.LENGTH_SHORT).show()
                finish()
            } else {

                photoAdapter.submitList(favorites)
                photoAdapter.setFavorites(favorites)
            }
        }
    }
}