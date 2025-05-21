package com.tushar.wallpapers.ui

import CategoryAdapter
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

import com.tushar.wallpapers.adapter.PhotoAdapter
import com.tushar.wallpapers.databinding.FragmentWallpaperBinding

import com.tushar.wallpapers.viewmodel.WallpaperViewModel

class WallpaperFragment : Fragment() {

    private lateinit var binding: FragmentWallpaperBinding
    private lateinit var viewModel: WallpaperViewModel
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWallpaperBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[WallpaperViewModel::class.java]

        setupCategoryRecyclerView()
        observeCategoryList()
        observeWallpaperList()

        return binding.root
    }

    private fun setupCategoryRecyclerView() {
        categoryAdapter = CategoryAdapter { category ->
            viewModel.fetchWallpapers(category.name)
        }
        binding.categoryRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.categoryRecyclerView.adapter = categoryAdapter
    }

    private fun observeCategoryList() {
        viewModel.categories.observe(viewLifecycleOwner) {
            categoryAdapter.submitList(it)
        }
    }

    private fun observeWallpaperList() {
        viewModel.wallpapers.observe(viewLifecycleOwner) { photoList ->
            val photoAdapter = PhotoAdapter(photoList) { clickedPhoto ->
                showWallpaperOptions(clickedPhoto.src.portrait)
            }

            binding.categoryRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
            binding.categoryRecyclerView.adapter = photoAdapter
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
