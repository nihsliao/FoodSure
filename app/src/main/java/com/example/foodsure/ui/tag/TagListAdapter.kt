package com.example.foodsure.ui.tag

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foodsure.databinding.TagListItemBinding

class TagListAdapter(
    private val onItemClicked: (String) -> Unit
) : ListAdapter<String, TagListAdapter.TagViewHolder>(TagDiffCallback) {
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
        fun bind(current: String) {
            binding.tagItemName.text = current
        }
    }

    object TagDiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }
    }
}

