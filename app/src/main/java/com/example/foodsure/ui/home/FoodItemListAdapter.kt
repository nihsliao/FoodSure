package com.example.foodsure.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foodsure.R
import com.example.foodsure.data.FoodItemKeys
import com.example.foodsure.databinding.FoodListItemBinding

class FoodItemListAdapter(
    private val onItemClicked: (Map<String, String>) -> Unit
) : ListAdapter<Map<String, String>, FoodItemListAdapter.FoodItemViewHolder>(FoodItemDiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodItemViewHolder {
        val binding =
            FoodListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodItemViewHolder, position: Int) {
        val current = getItem(position) // Should not pass position to action listeners
        holder.bind(current)
        holder.itemView.setOnClickListener {
            onItemClicked(current)
        }
    }

    /**
     * Provide a reference to the type of views that you are using
     */
    class FoodItemViewHolder(val binding: FoodListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        companion object

        fun bind(item: Map<String, String>) {
            binding.name.text = item[FoodItemKeys.NAME]
            binding.category.text = item[FoodItemKeys.CATEGORY]
            binding.expiredDate.text = itemView.context.getString(
                R.string.expires_on,
                item[FoodItemKeys.EXPIRED]
            )
            binding.quantity.text = item[FoodItemKeys.QUANTITY]
            binding.storageLocation.text = item[FoodItemKeys.STORAGE]
            binding.tags.text = item[FoodItemKeys.TAGS]
        }
    }

    object FoodItemDiffCallback : DiffUtil.ItemCallback<Map<String, String>>() {
        override fun areItemsTheSame(
            oldItem: Map<String, String>,
            newItem: Map<String, String>
        ): Boolean {
            return oldItem[FoodItemKeys.ID] == newItem[FoodItemKeys.ID]
        }

        override fun areContentsTheSame(
            oldItem: Map<String, String>,
            newItem: Map<String, String>
        ): Boolean {
            if (oldItem.size != newItem.size) return false
            for (item in oldItem) {
                if (newItem[item.key] != item.value) return false
            }
            return true
        }
    }
}
