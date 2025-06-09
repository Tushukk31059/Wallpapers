package com.tushar.wallpapers

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.tushar.wallpapers.adapter.CategoryAdapter
import com.tushar.wallpapers.adapter.PhotoAdapter
import com.tushar.wallpapers.databinding.ActivityMainBinding
import com.tushar.wallpapers.model.Photo
import com.tushar.wallpapers.model.WallpaperDatabase
import com.tushar.wallpapers.viewmodel.WallpaperViewModel


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: WallpaperViewModel by viewModels()
    private lateinit var photoAdapter: PhotoAdapter
    private lateinit var categoryAdapter: CategoryAdapter

    // Favorite list stored in memory
    private val favoriteList = mutableListOf<Photo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
window.navigationBarColor=ContextCompat.getColor(this,R.color.grey)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val selectedId = intent.getIntExtra("selected_id", R.id.nav_home)

        binding.navView.post {
            binding.navView.menu.setGroupCheckable(0, true, true)
            binding.navView.setCheckedItem(selectedId)
            binding.navView.menu.findItem(selectedId)?.isChecked = true
        }
        setupAdapters()
        setupObservers()
        setupClickListeners()

        viewModel.loadCategories()
        viewModel.fetchWallpapers("nature")

        onBackPressedDispatcher.addCallback(this) {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                finishAffinity()
            }}
    }

    private fun setupAdapters() {
        categoryAdapter = CategoryAdapter { category ->
            viewModel.fetchWallpapers(category.name)
        }

        binding.categoryRecyclerView.apply {
            layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
        }
        photoAdapter = PhotoAdapter(this,viewModel)
        binding.wallpaperRecyclerView.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 2)
            adapter = photoAdapter
        }
    }

    private fun setupObservers() {
        viewModel.categories.observe(this) { categories ->
            categoryAdapter.submitList(categories)
        }

        viewModel.wallpapers.observe(this) { photos ->
            photoAdapter.submitList(photos)
            photoAdapter.setFavorites(viewModel.allFavorites.value ?: emptyList())
        }
        viewModel.allFavorites.observe(this) { favoriteList ->
            photoAdapter.setFavorites(favoriteList)
            photoAdapter.notifyDataSetChanged()
        }
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        viewModel.errorMessage.observe(this) { message ->
            if (!message.isNullOrEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupClickListeners() {
        binding.menu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {

                R.id.nav_favourites -> {
                    val intent = Intent(this, FavouriteActivity::class.java)
                    intent.putParcelableArrayListExtra("favorites", ArrayList(favoriteList))
                    startActivity(intent)
                    finish()
                }
                R.id.nav_about -> {
                    val intent= Intent(this,AboutUsAct::class.java)
                    intent.putParcelableArrayListExtra("favorites", ArrayList(favoriteList))
                    intent.putExtra("fav_selected",R.id.nav_favourites)
                    startActivity(intent)

                }
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }
}