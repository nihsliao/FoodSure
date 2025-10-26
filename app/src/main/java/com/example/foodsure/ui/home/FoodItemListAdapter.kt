package com.example.foodsure.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foodsure.R
import com.example.foodsure.data.FoodItemWithTags
import com.example.foodsure.databinding.FoodListItemBinding
import java.text.SimpleDateFormat
import java.util.Locale

class FoodItemListAdapter(
    private val onItemClicked: (FoodItemWithTags) -> Unit
) : ListAdapter<FoodItemWithTags, FoodItemListAdapter.FoodItemViewHolder>(FoodItemDiffCallback) {
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

        fun bind(item: FoodItemWithTags) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            val foodItem = item.foodItem
            binding.name.text = foodItem.name
            binding.category.text = foodItem.category
            binding.expiredDate.text = itemView.context.getString(
                R.string.expires_on,
                dateFormat.format(foodItem.expiration)
            )
            binding.quantity.text = foodItem.quantity.toString()
            binding.storageLocation.text = foodItem.storage
            binding.tags.text = item.tags.joinToString(", ") { tag -> tag.name }
        }
    }

    object FoodItemDiffCallback : DiffUtil.ItemCallback<FoodItemWithTags>() {
        override fun areItemsTheSame(
            oldItem: FoodItemWithTags,
            newItem: FoodItemWithTags
        ): Boolean {
            return oldItem.foodItem.id == newItem.foodItem.id
        }

        override fun areContentsTheSame(
            oldItem: FoodItemWithTags,
            newItem: FoodItemWithTags
        ): Boolean {
            return oldItem == newItem
        }
    }
}
