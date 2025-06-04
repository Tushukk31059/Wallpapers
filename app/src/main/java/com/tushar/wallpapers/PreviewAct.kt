package com.tushar.wallpapers

import android.app.WallpaperManager
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.tushar.wallpapers.databinding.ActivityPreviewBinding
import com.tushar.wallpapers.databinding.ActivityWallpaperPreviewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStream

class PreviewAct : AppCompatActivity() {
    private lateinit var binding: ActivityPreviewBinding
    private var currentBitmap: Bitmap? = null
    private var isLoading = false
    override fun onCreate(savedInstanceState: Bundle?) {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

// Optional: make sure status bar and navigation bar are transparent
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.backBtn.setOnClickListener {
            if (!isLoading) finish()
        }

        val imageUrl = intent.getStringExtra("image_url") ?: run {
            finish()
            return
        }

        loadImage(imageUrl)
        setupWallpaperButton()
        binding.downloadButton.setOnClickListener{
            currentBitmap?.let { it1 -> saveImage(this, it1,"Wallpaper") }
            Toast.makeText(this,"Wallpaper Saved Successfully",Toast.LENGTH_SHORT).show()
        }
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
                    Toast.makeText(this@PreviewAct,
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
    private fun saveImage(context:Context, bitmap: Bitmap,fileName:String):Uri?{
        val fileName="$fileName.jpg"
        val fileOutputStream:OutputStream?
        val imageUri:Uri?
        val contentValues=ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME,fileName)
            put(MediaStore.Images.Media.MIME_TYPE,"images/jpg")
            put(MediaStore.Images.Media.RELATIVE_PATH,Environment.DIRECTORY_PICTURES)
            put(MediaStore.Images.Media.IS_PENDING,1)
        }
        val contentResolver=context.contentResolver
        imageUri=contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues)
        if (imageUri != null){
            fileOutputStream=contentResolver.openOutputStream(imageUri)
            if (fileOutputStream != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream)
            }
            fileOutputStream?.flush()
            fileOutputStream?.close()
            contentValues.clear()
            contentValues.put(MediaStore.Images.Media.IS_PENDING,0)
            contentResolver.update(imageUri,contentValues,null,null)
        }
        return imageUri


    }
    private fun setAsWallpaper(bitmap: Bitmap) {
        isLoading = true
        binding.setWallpaperBtn.isEnabled = false
        binding.progressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                WallpaperManager.getInstance(this@PreviewAct).setBitmap(bitmap)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PreviewAct,
                        "Wallpaper set successfully!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PreviewAct,
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
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }
    }

}