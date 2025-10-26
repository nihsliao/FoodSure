package com.example.foodsure.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foodsure.R
import com.example.foodsure.data.FoodItemWithTags
import com.example.foodsure.databinding.FragmentHomeBinding
import com.example.foodsure.ui.BaseListFragment
import com.example.foodsure.ui.BaseViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class HomeFragment : BaseListFragment<FragmentHomeBinding, FoodItemWithTags, HomeViewModel>() {
    override val viewModel: HomeViewModel by activityViewModels {
        BaseViewModel.provideFactory(repository, {
            HomeViewModel(repository)
        })
    }

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

    private fun navigateToEdit(id: Long, name: String) {
        val action = HomeFragmentDirections.actionAddFoodItem(id, name)
        findNavController().navigate(action)
    }

    override val listAdapter: ListAdapter<FoodItemWithTags, *> by lazy {
        FoodItemListAdapter({ item ->
            navigateToEdit(
                item.foodItem.id,
                item.foodItem.name
            )
        })
    }
    override val recyclerView: RecyclerView
        get() = binding.itemRecycleView
    override val listData: LiveData<out List<FoodItemWithTags>>
        get() = viewModel.foodList

    override fun showDeletionDialog(
        position: Int,
        item: FoodItemWithTags
    ) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(getString(R.string.delete_dialog_message, item.foodItem.name))
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