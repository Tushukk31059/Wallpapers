package com.tushar.wallpapers.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tushar.wallpapers.R
import com.tushar.wallpapers.model.Category


class CategoryAdapter(
private val onItemClick: (Category) -> Unit
) : ListAdapter<Category, CategoryAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    private var selectedPosition: Int = 0 // Default selection at position 0

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.categoryName)
        private val layout: View = itemView

        fun bind(category: Category, isSelected: Boolean) {
            textView.text = category.name

            val bgRes = if (isSelected) {
                R.drawable.category_bg_selected
            } else {
                R.drawable.category_bg
            }
            layout.setBackgroundResource(bgRes)

            itemView.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = adapterPosition

                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)

                onItemClick(category)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val isSelected = position == selectedPosition
        holder.bind(getItem(position), isSelected)
    }

    // Optional: override submitList to auto-click first item
    override fun submitList(list: List<Category>?) {
        super.submitList(list)
        if (!list.isNullOrEmpty()) {
            // Forcefully trigger the click callback if needed:
            onItemClick(list[0])
        }
    }

    private class CategoryDiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem == newItem
        }
    }
}
