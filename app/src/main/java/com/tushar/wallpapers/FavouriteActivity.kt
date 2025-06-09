package com.tushar.wallpapers

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.lifecycle.Observer
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
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
        binding.navView.menu.setGroupCheckable(0, true, true)
        binding.navView.setCheckedItem(R.id.nav_favourites)
        setupListeners()
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[WallpaperViewModel::class.java]
        photoAdapter = PhotoAdapter(this, viewModel)

        binding.favoriteRecyclerView.layoutManager = GridLayoutManager(this@FavouriteActivity, 2)
        binding.favoriteRecyclerView.adapter = photoAdapter
        onBackPressedDispatcher.addCallback(this) {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                val intent = Intent(this@FavouriteActivity, MainActivity::class.java)
                intent.putParcelableArrayListExtra("favorites", ArrayList(favoriteList))
                intent.putExtra("selected_id", R.id.nav_home)
                startActivity(intent)
                finish()
            }
        }


        // Observe Favorites from Room DB
        viewModel.allFavorites.observe(this) { favorites ->
            if (favorites.isEmpty()) {


                binding.constraint.visibility = View.VISIBLE
                binding.favoriteRecyclerView.visibility = View.GONE
                binding.animationView.playAnimation()
            } else {
                binding.constraint.visibility = View.GONE
                binding.favoriteRecyclerView.visibility = View.VISIBLE

                binding.animationView.cancelAnimation()
                photoAdapter.setFavorites(favorites)
                photoAdapter.submitList(favorites.toList())

            }
        }

    }

    private fun setupListeners() {

        binding.menu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {

                    val intent = Intent(this, MainActivity::class.java)
                    intent.putParcelableArrayListExtra("favorites", ArrayList(favoriteList))
                    intent.putExtra("selected_id", R.id.nav_home)
                    startActivity(intent)
                    binding.animationView.cancelAnimation()
                    finish()
                }

                R.id.nav_about -> {
                    val intent = Intent(this, AboutUsAct::class.java)
                    intent.putParcelableArrayListExtra("favorites", ArrayList(favoriteList))
                    binding.animationView.cancelAnimation()
                    startActivity(intent)

                }
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }
}
