package com.tushar.wallpapers.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tushar.wallpapers.PreviewAct
import com.tushar.wallpapers.R
import com.tushar.wallpapers.databinding.ItemWallpaperBinding
import com.tushar.wallpapers.model.Photo

class PhotoAdapter(

    private val favoritePhotos: MutableList<Photo>,
    private val onItemClick: (Photo) -> Unit,
    private val onFavoriteClick: (Photo) -> Unit
) : ListAdapter<Photo, PhotoAdapter.PhotoViewHolder>(PhotoDiffCallback()) {

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
        Glide.with(holder.itemView.context)
            .load(photo.src.portrait)
            .placeholder(R.drawable.placeholder)
            .into(holder.binding.wallpaperImage)

        // In your PhotoAdapter's onBindViewHolder:
        holder.binding.wallpaperImage.setOnClickListener {
            val intent = Intent(holder.itemView.context, PreviewAct::class.java).apply {
                putExtra("image_url", photo.src.portrait)
            }
            holder.itemView.context.startActivity(intent)
        }
        holder.binding.favoriteButton.setImageResource(
            if (favoritePhotos.contains(photo)) R.drawable.star_blue else R.drawable.star_black
        )

        holder.binding.favoriteButton.setOnClickListener {
            if (favoritePhotos.contains(photo)) {
                favoritePhotos.remove(photo)
                holder.binding.favoriteButton.setImageResource(R.drawable.star_black)
            } else {
                favoritePhotos.add(photo)
                holder.binding.favoriteButton.setImageResource(R.drawable.star_blue)
            }
            onFavoriteClick(photo) // Optional: Notify outside if needed
        }


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