package com.tushar.wallpapers

import CategoryAdapter
import android.app.WallpaperManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide

import com.tushar.wallpapers.databinding.ActivityMainBinding
import com.tushar.wallpapers.model.Category
import com.tushar.wallpapers.model.Photo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import com.tushar.wallpapers.adapter.PhotoAdapter
import com.tushar.wallpapers.api.ApiUtilities
import com.tushar.wallpapers.model.SearchResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var photoAdapter: PhotoAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private val photoList = ArrayList<Photo>()
    private val categoryList = ArrayList<Category>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupCategoryRecyclerView()
        setupWallpaperRecyclerView(photoList)

        loadCategories()
        loadWallpapers("nature") // Default category
    }

    private fun setupCategoryRecyclerView() {
        categoryAdapter = CategoryAdapter() { category ->
            loadWallpapers(category.name)
        }
        binding.categoryRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.categoryRecyclerView.adapter = categoryAdapter
    }

    private fun setupWallpaperRecyclerView(photoList: List<Photo>) {
        photoAdapter = PhotoAdapter(photoList) { photo ->
            setWallpaperFromUrl(photo.src.portrait)
        }
        binding.wallpaperRecyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.wallpaperRecyclerView.adapter = photoAdapter
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

    private fun loadCategories() {
        categoryList.clear()
        categoryList.add(
            Category(
                "Nature",
                "https://images.pexels.com/photos/417173/pexels-photo-417173.jpeg"
            )
        )
        categoryList.add(
            Category(
                "Cars",
                "https://images.pexels.com/photos/210019/pexels-photo-210019.jpeg"
            )
        )
        categoryList.add(
            Category(
                "Animals",
                "https://images.pexels.com/photos/145939/pexels-photo-145939.jpeg"
            )
        )
        categoryList.add(
            Category(
                "Technology",
                "https://images.pexels.com/photos/373543/pexels-photo-373543.jpeg"
            )
        )
        categoryList.add(
            Category(
                "Travel",
                "https://images.pexels.com/photos/672358/pexels-photo-672358.jpeg"
            )
        )
        categoryList.add(
            Category(
                "Food",
                "https://images.pexels.com/photos/70497/pexels-photo-70497.jpeg"
            )
        )

        categoryAdapter.notifyDataSetChanged()
    }

    private fun loadWallpapers(query: String) {
        ApiUtilities.getApiInterface().getSearchImage(query, 50)
            .enqueue(object : Callback<SearchResult> {
                override fun onResponse(
                    call: Call<SearchResult>,
                    response: Response<SearchResult>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        photoList.clear()
                        photoList.addAll(response.body()!!.photos)
                        photoAdapter.notifyDataSetChanged()
                    }
                }

                override fun onFailure(
                    call: Call<com.tushar.wallpapers.model.SearchResult>,
                    t: Throwable
                ) {
                    Toast.makeText(
                        this@MainActivity,
                        "Failed to load wallpapers",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

}
