package com.tushar.wallpapers.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tushar.wallpapers.PreviewAct
import com.tushar.wallpapers.R
import com.tushar.wallpapers.databinding.ItemWallpaperBinding
import com.tushar.wallpapers.model.Photo
import com.tushar.wallpapers.viewmodel.WallpaperViewModel

class PhotoAdapter(
    private val context: Context,private val viewModel: WallpaperViewModel,


) : ListAdapter<Photo, PhotoAdapter.PhotoViewHolder>(PhotoDiffCallback()) {

    private var favoritePhotos: List<Photo> = emptyList()


    inner class PhotoViewHolder(val binding: ItemWallpaperBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = ItemWallpaperBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo = getItem(position)

        // Load image using Glide
        Glide.with(holder.itemView.context)
            .load(photo.src.portrait)
            .placeholder(R.drawable.placeholder)
            .into(holder.binding.wallpaperImage)

        // Handle wallpaper click to open PreviewAct
        holder.binding.wallpaperImage.setOnClickListener {
            val intent = Intent(holder.itemView.context, PreviewAct::class.java).apply {
                putExtra("image_url", photo.src.portrait)
            }
            holder.itemView.context.startActivity(intent)
        }

        // Check if current photo is in favorites
        val isFavorite = favoritePhotos.any { fav -> fav.id == photo.id }


        // Set the favorite button icon accordingly
        holder.binding.favoriteButton.setImageResource(
            if (isFavorite) R.drawable.star_blue else R.drawable.star_black
        )

        // Handle favorite button click
        holder.binding.favoriteButton.setOnClickListener {
            if (isFavorite) {
                viewModel.removeFromFavorites(photo)

                Toast.makeText(context, "Removed from Favourites", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.addToFavorites(photo)
                Toast.makeText(context, "Added to Favourites", Toast.LENGTH_SHORT).show()
            }

            // Notify adapter and invoke callback
            submitList(currentList.toList()) // force DiffUtil re-evaluation


        }
    }

    fun setFavorites(favorites: List<Photo>) {
        favoritePhotos = favorites
        notifyDataSetChanged()
    }

    class PhotoDiffCallback : DiffUtil.ItemCallback<Photo>() {
        override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem == newItem
        }
    }
}
