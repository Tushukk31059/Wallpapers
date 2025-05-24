package com.tushar.wallpapers

import android.app.WallpaperManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.tushar.wallpapers.adapter.CategoryAdapter
import com.tushar.wallpapers.adapter.PhotoAdapter
import com.tushar.wallpapers.databinding.ActivityMainBinding
import com.tushar.wallpapers.viewmodel.WallpaperViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: WallpaperViewModel by viewModels()
    private lateinit var photoAdapter: PhotoAdapter
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupAdapters()
        setupObservers()
        viewModel.loadCategories()
        viewModel.fetchWallpapers("nature") // Default category
    }

    private fun setupAdapters() {
        categoryAdapter = CategoryAdapter { category ->
            viewModel.fetchWallpapers(category.name)
        }

        photoAdapter = PhotoAdapter { photo ->
            setWallpaperFromUrl(photo.src.portrait)
        }

        binding.categoryRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
        }

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

    private fun setWallpaperFromUrl(imageUrl: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val bitmap = Glide.with(this@MainActivity)
                    .asBitmap()
                    .load(imageUrl)
                    .submit()
                    .get()

                val wallpaperManager = WallpaperManager.getInstance(applicationContext)
                wallpaperManager.setBitmap(bitmap)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Wallpaper Set!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Failed to set wallpaper", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}