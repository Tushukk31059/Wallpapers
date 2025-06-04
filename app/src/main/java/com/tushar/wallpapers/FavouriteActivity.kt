package com.tushar.wallpapers

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.tushar.wallpapers.adapter.PhotoAdapter

import com.tushar.wallpapers.databinding.ActivityFavouriteBinding
import com.tushar.wallpapers.model.Photo

class FavouriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavouriteBinding
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
        val favorites = intent.getParcelableArrayListExtra<Photo>("favorites")
        if (favorites.isNullOrEmpty()) {
            Toast.makeText(this, "No favorites found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        photoAdapter =PhotoAdapter(

            favoritePhotos = favoriteList,
            onItemClick = { photo ->
                Toast.makeText(this, "Clicked on favorite photo ID: ${photo.id}", Toast.LENGTH_SHORT).show()
                // You can also launch PreviewAct or do other actions here
            },
            onFavoriteClick = { photo ->
                if (favoriteList.contains(photo)) {
                    favoriteList.remove(photo)
                    Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show()
                } else {
                    favoriteList.add(photo)
                    Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show()
                }

                photoAdapter.notifyDataSetChanged() // Refresh icons
            }
        )

        binding.favoriteRecyclerView.layoutManager = GridLayoutManager(this@FavouriteActivity, 2)
            binding.favoriteRecyclerView.adapter = photoAdapter
       photoAdapter.submitList(favorites)
        }

    }
