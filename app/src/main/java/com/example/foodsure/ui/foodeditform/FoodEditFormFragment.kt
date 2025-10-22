package com.example.foodsure.ui.foodeditform

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.children
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.foodsure.R
import com.example.foodsure.data.FoodItemKeys
import com.example.foodsure.databinding.FragmentEditFoodBinding
import com.example.foodsure.ui.BaseFragment
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date

class FoodEditFormFragment() : BaseFragment<FragmentEditFoodBinding, FoodEditFormViewModel>() {
    override val viewModel: FoodEditFormViewModel by viewModels()
    private val navigationArgs: FoodEditFormFragmentArgs by navArgs()

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEditFoodBinding {
        return FragmentEditFoodBinding.inflate(inflater, container, false)
    }

    override fun setupUI() {
        // Observe foodItem and navigation
        viewModel.foodItem.observe(viewLifecycleOwner) { bindItem(it) }
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

        binding.editExpirationDate.setOnClickListener { showDatePicker() }
        binding.editButtonSave.setOnClickListener { onSave() }
        setupAutoCompleteAdapter(
            viewModel.getTags(), binding.editTags,
            { it.contains(binding.editTags.text.toString().trim()) })

        if (navigationArgs.foodItemId != -1L) {
            viewModel.loadFoodItem(navigationArgs.foodItemId.toString())
        }
    }

    private fun bindItem(item: Map<String, String>) {
        binding.editName.setText(item[FoodItemKeys.NAME])
        binding.editCategory.setText(item[FoodItemKeys.CATEGORY])
        binding.editQuantity.setText(item[FoodItemKeys.QUANTITY])
        binding.editStorage.setText(item[FoodItemKeys.STORAGE])

        item[FoodItemKeys.TAGS].toString().split(",").forEach { tag ->
            addTagToChipGroup(tag)
        }
    }

    private fun onSave() {
        viewModel.saveFoodItem(
            binding.editName.text.toString(),
            binding.editCategory.text.toString(),
            binding.editQuantity.text.toString(),
            binding.editStorage.text.toString(),
            binding.chipGroupTags.children.filterIsInstance<Chip>()
                .joinToString(",") { it.text.toString() }
        )
    }

    private fun <T> setupAutoCompleteAdapter(
        liveList: LiveData<List<T>>,
        view: MaterialAutoCompleteTextView,
        predicate: (T) -> Boolean
    ) {
        val listAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            mutableListOf<T>()
        )

        view.setAdapter(listAdapter)
        var originalList: List<T> = emptyList()
        view.doAfterTextChanged { text ->
            lifecycleScope.launch {
                delay(300)
                val filteredList = originalList.filter { predicate(it) }
                listAdapter.clear()
                listAdapter.addAll(filteredList)
                listAdapter.notifyDataSetChanged()
            }
        }
        view.setOnItemClickListener { adapterView, _, position, _ ->
            val selectedTag = adapterView.getItemAtPosition(position) as String
            addTagToChipGroup(selectedTag)
            view.text = null
        }

        liveList.observe(viewLifecycleOwner) { list ->
            originalList = list
            listAdapter.clear()
            listAdapter.addAll(list)
            listAdapter.notifyDataSetChanged()
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
            .setSelection(viewModel.expirationDateLong)
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val date = Date(selection)
            viewModel.setExpirationDate(date)
        }
        datePicker.show(parentFragmentManager, "DATE_PICKER")
    }
}