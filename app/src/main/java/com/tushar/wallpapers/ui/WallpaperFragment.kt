package com.tushar.wallpapers.ui

import android.app.WallpaperManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.tushar.wallpapers.adapter.CategoryAdapter

import com.tushar.wallpapers.adapter.PhotoAdapter
import com.tushar.wallpapers.databinding.FragmentWallpaperBinding
import com.tushar.wallpapers.model.Photo

import com.tushar.wallpapers.viewmodel.WallpaperViewModel

class WallpaperFragment : Fragment() {
    private lateinit var binding: FragmentWallpaperBinding
    private lateinit var viewModel: WallpaperViewModel
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var photoAdapter: PhotoAdapter


    private val favoriteList = mutableListOf<Photo>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWallpaperBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[WallpaperViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapters()
        setupRecyclerViews()
        setupObservers()

        viewModel.loadCategories()
        viewModel.fetchWallpapers("Nature") // Default category
    }

    private fun setupAdapters() {
        categoryAdapter = CategoryAdapter { category ->
            viewModel.fetchWallpapers(category.name)
        }

        photoAdapter = PhotoAdapter(

            favoritePhotos = favoriteList,
            onItemClick = { clickedPhoto ->
                showWallpaperOptions(clickedPhoto.src.portrait)
            },
            onFavoriteClick = { photo ->
                // Handle favorite toggle here, e.g. add or remove from favorites
                Toast.makeText(requireContext(), "Favorite clicked: ${photo.id}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun setupRecyclerViews() {
        // Category RecyclerView (horizontal)
        binding.categoryRecyclerView.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = categoryAdapter
        }

        // Wallpaper RecyclerView (grid)
        binding.categoryRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = photoAdapter
        }
    }

    private fun setupObservers() {
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            categoryAdapter.submitList(categories)
        }

        viewModel.wallpapers.observe(viewLifecycleOwner) { wallpapers ->
            photoAdapter.submitList(wallpapers)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showWallpaperOptions(imageUrl: String) {
        val options = arrayOf("Set as Home Screen", "Set as Lock Screen", "Set as Both", "Cancel")
        AlertDialog.Builder(requireContext())
            .setTitle("Set Wallpaper")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> setWallpaper(imageUrl, WallpaperManager.FLAG_SYSTEM)
                    1 -> setWallpaper(imageUrl, WallpaperManager.FLAG_LOCK)
                    2 -> {
                        setWallpaper(imageUrl, WallpaperManager.FLAG_SYSTEM)
                        setWallpaper(imageUrl, WallpaperManager.FLAG_LOCK)
                    }
                    3 -> dialog.dismiss()
                }
            }
            .show()
    }

    private fun setWallpaper(imageUrl: String, flag: Int) {
        Glide.with(requireContext())
            .asBitmap()
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    try {
                        val wallpaperManager = WallpaperManager.getInstance(requireContext())
                        wallpaperManager.setBitmap(resource, null, true, flag)
                        Toast.makeText(requireContext(), "Wallpaper Set", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(requireContext(), "Error setting wallpaper", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }
}
