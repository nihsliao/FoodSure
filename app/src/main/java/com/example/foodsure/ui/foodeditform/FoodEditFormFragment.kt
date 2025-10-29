package com.example.foodsure.ui.foodeditform

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.foodsure.R
import com.example.foodsure.data.FoodItemWithTags
import com.example.foodsure.databinding.FragmentEditFoodBinding
import com.example.foodsure.ui.BaseFragment
import com.example.foodsure.ui.BaseViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import java.util.Date

class FoodEditFormFragment() : BaseFragment<FragmentEditFoodBinding, FoodEditFormViewModel>() {
    override val viewModel: FoodEditFormViewModel by activityViewModels {
        BaseViewModel.provideFactory(repository, {
            FoodEditFormViewModel(repository)
        })
    }

    private val navigationArgs: FoodEditFormFragmentArgs by navArgs()

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEditFoodBinding {
        return FragmentEditFoodBinding.inflate(inflater, container, false)
    }

    override fun setupUI() {
        // Observe foodItem and navigation
        viewModel.foodItem.observe(viewLifecycleOwner) { if (it != null) bindItem(it) }
        viewModel.expirationDateString.observe(viewLifecycleOwner) {
            binding.editExpirationDate.text =
                getString(R.string.expires_on, it)
        }
        viewModel.navigateToHome.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigateUp()
                viewModel.onDoneNavigation()
            }
        }
        viewModel.toastMsg.observe(viewLifecycleOwner) {
            if (it.isNotBlank()) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                viewModel.onDoneToast()
            }
        }

        binding.editExpirationDate.setOnClickListener { showDatePicker() }
        binding.editButtonSave.setOnClickListener { onSave() }
        setupAutoCompleteAdapter(
            binding.editTags,
            { it.contains(binding.editTags.text.toString().trim(), true) })

        if (navigationArgs.foodItemId != -1L) {
            viewModel.loadFoodItem(navigationArgs.foodItemId)
        } else {
            viewModel.clearFormState()
        }
    }

    private fun bindItem(item: FoodItemWithTags) {
        val foodItem = item.foodItem
        binding.editName.setText(foodItem.name)
        binding.editCategory.setText(foodItem.category)
        binding.editQuantity.setText(foodItem.quantity.toString())
        binding.editStorage.setText(foodItem.storage)
        viewModel.setExpirationDate(foodItem.expiration)

        item.tags.forEach {
            addTagToChipGroup(it.name)
        }
    }

    private fun onSave() {
        viewModel.saveFoodItem(
            navigationArgs.foodItemId.takeIf { it != -1L },
            binding.editName.text.toString(),
            binding.editCategory.text.toString(),
            viewModel.expirationDate.value ?: Date(),
            binding.editQuantity.text.toString().toDoubleOrNull() ?: 0.0,
            binding.editStorage.text.toString(),
            binding.chipGroupTags.children.filterIsInstance<Chip>().map { it.text.toString() }
                .toList()
        )
    }

    private fun setupAutoCompleteAdapter(
        view: MaterialAutoCompleteTextView,
        predicate: (String) -> Boolean
    ) {
        val listAdapter = TagArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            viewModel.allTags.value?.toMutableList() ?: mutableListOf(),
            predicate
        )
        view.setAdapter(listAdapter)

        viewModel.allTags.observe(viewLifecycleOwner) {
            listAdapter.updateItemList(it)
        }

        view.setOnItemClickListener { adapterView, _, position, _ ->
            val selectedTag = adapterView.getItemAtPosition(position) as String
            addTagToChipGroup(selectedTag)
            view.text = null
        }
    }

    private fun addTagToChipGroup(tag: String) {
        val isTagAlreadyAdded = binding.chipGroupTags.children
            .filterIsInstance<Chip>()
            .any { it.text == tag }
        if (isTagAlreadyAdded) return

        val chip = Chip(requireContext()).apply {
            text = tag
            isCloseIconVisible = true
            setOnCloseIconClickListener {
                binding.chipGroupTags.removeView(this)
            }
        }
        binding.chipGroupTags.addView(chip)
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select expiration date")
            .setSelection(
                viewModel.expirationDate.value?.time ?: MaterialDatePicker.todayInUtcMilliseconds()
            )
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val date = Date(selection)
            viewModel.setExpirationDate(date)
        }
        datePicker.show(parentFragmentManager, "DATE_PICKER")
    }
}