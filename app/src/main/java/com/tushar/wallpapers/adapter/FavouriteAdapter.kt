package com.tushar.wallpapers.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tushar.wallpapers.R
import com.tushar.wallpapers.databinding.ItemWallpaperBinding
import com.tushar.wallpapers.model.Photo

class FavouriteAdapter : ListAdapter<Photo, FavouriteAdapter.FavViewHolder>(FavDiffCallback()) {

    inner class FavViewHolder(val binding: ItemWallpaperBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavViewHolder {
        val binding = ItemWallpaperBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FavViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavViewHolder, position: Int) {
        val photo = getItem(position)

        Glide.with(holder.itemView.context)
            .load(photo.src.portrait)
            .placeholder(R.drawable.placeholder)
            .into(holder.binding.wallpaperImage)

        // Hide or disable favorite button if needed
        holder.binding.favoriteButton.setImageResource(R.drawable.star_blue)
        holder.binding.favoriteButton.isEnabled = false
    }

    class FavDiffCallback : DiffUtil.ItemCallback<Photo>() {
        override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem == newItem
        }
    }
}
