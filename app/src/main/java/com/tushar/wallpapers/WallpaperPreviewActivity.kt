package com.tushar.wallpapers

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.app.WallpaperManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.tushar.wallpapers.databinding.ActivityWallpaperPreviewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WallpaperPreviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWallpaperPreviewBinding
    private var currentBitmap: Bitmap? = null
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        binding = ActivityWallpaperPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set up back button
        binding.backBtn.setOnClickListener {
            if (!isLoading) finish()
        }

        val imageUrl = intent.getStringExtra("image_url") ?: run {
            finish()
            return
        }

        loadImage(imageUrl)
        setupWallpaperButton()
    }

    private fun loadImage(imageUrl: String) {
        binding.progressBar.visibility = View.VISIBLE

        Glide.with(this)
            .asBitmap()
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    currentBitmap = resource
                    binding.imageView.setImageBitmap(resource)
                    binding.progressBar.visibility = View.GONE
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@WallpaperPreviewActivity,
                        "Failed to load image", Toast.LENGTH_SHORT).show()
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    currentBitmap = null
                }
            })
    }

    private fun setupWallpaperButton() {
        binding.setWallpaperBtn.setOnClickListener {
            if (isLoading) return@setOnClickListener

            currentBitmap?.let { bitmap ->
                setAsWallpaper(bitmap)
            } ?: run {
                Toast.makeText(this, "Image not loaded yet", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setAsWallpaper(bitmap: Bitmap) {
        isLoading = true
        binding.setWallpaperBtn.isEnabled = false
        binding.progressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                WallpaperManager.getInstance(this@WallpaperPreviewActivity).setBitmap(bitmap)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@WallpaperPreviewActivity,
                        "Wallpaper set successfully!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@WallpaperPreviewActivity,
                        "Failed to set wallpaper: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } finally {
                withContext(Dispatchers.Main) {
                    isLoading = false
                    binding.setWallpaperBtn.isEnabled = true
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }
}