package com.example.foodsure.ui.tag

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foodsure.data.FoodTag
import com.example.foodsure.databinding.TagListItemBinding

class TagListAdapter(
    private val onItemClicked: (FoodTag) -> Unit
) : ListAdapter<FoodTag, TagListAdapter.TagViewHolder>(TagDiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TagViewHolder {
        val binding = TagListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TagViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: TagViewHolder,
        position: Int
    ) {
        val current = getItem(position)
        holder.bind(current)
        holder.itemView.setOnClickListener {
            onItemClicked(current)
        }
    }

    class TagViewHolder(val binding: TagListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(current: FoodTag) {
            binding.tagItemName.text = current.name
        }
    }

    object TagDiffCallback : DiffUtil.ItemCallback<FoodTag>() {
        override fun areItemsTheSame(
            oldItem: FoodTag,
            newItem: FoodTag
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: FoodTag,
            newItem: FoodTag
        ): Boolean {
            return oldItem == newItem
        }
    }
}

