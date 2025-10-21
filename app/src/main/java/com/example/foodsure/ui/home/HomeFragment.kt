package com.example.foodsure.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foodsure.R
import com.example.foodsure.data.FoodItemKeys
import com.example.foodsure.databinding.FragmentHomeBinding
import com.example.foodsure.ui.BaseListFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class HomeFragment : BaseListFragment<FragmentHomeBinding, Map<String, String>, HomeViewModel>() {
    override val viewModel: HomeViewModel by viewModels()

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(layoutInflater, container, false)
    }

    override fun setupUI() {
        binding.fab.setOnClickListener({
            navigateToEdit(-1L, getString(R.string.add_food_item))
        })
    }

    private fun navigateToEdit(id: Long, name: String) {}

    override val listAdapter: ListAdapter<Map<String, String>, *> by lazy {
        FoodItemListAdapter({ item ->
            navigateToEdit(
                item[FoodItemKeys.ID]?.toLong() as Long,
                item[FoodItemKeys.NAME] as String
            )
        })
    }
    override val recyclerView: RecyclerView
        get() = binding.itemRecycleView
    override val listData: LiveData<out List<Map<String, String>>>
        get() = viewModel.foodList

    override fun showDeletionDialog(
        position: Int,
        item: Map<String, String>
    ) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(getString(R.string.delete_dialog_message, item[FoodItemKeys.NAME]))
            .setNegativeButton(getString(R.string.cancel)) { dialog, which ->
                listAdapter.notifyItemChanged(position)
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                viewModel.removeItem(item)
            }.show()
    }

    override fun handleSearchQuery(query: String) {
        viewModel.search(query)
    }
}