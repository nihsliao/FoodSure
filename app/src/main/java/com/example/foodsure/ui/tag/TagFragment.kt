package com.example.foodsure.ui.tag

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foodsure.R
import com.example.foodsure.databinding.FragmentTagBinding
import com.example.foodsure.ui.BaseListFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class TagFragment : BaseListFragment<FragmentTagBinding, String, TagViewModel>() {
    override val viewModel: TagViewModel by viewModels()
    override val listAdapter: ListAdapter<String, *> by lazy {
        TagListAdapter({
            showEditTagDialog(getString(R.string.edit_tag_title), it)
        })
    }
    override val recyclerView: RecyclerView
        get() = binding.tagRecycleView
    override val listData: LiveData<out List<String>>
        get() = viewModel.tagList

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTagBinding {
        return FragmentTagBinding.inflate(layoutInflater, container, false)
    }

    override fun setupUI() {
        binding.fab.setOnClickListener {
            showEditTagDialog(getString(R.string.add_tag_title), "")
        }
    }

    override fun showDeletionDialog(
        position: Int,
        item: String
    ) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(getString(R.string.delete_dialog_message, item))
            .setPositiveButton(R.string.delete) { _, _ ->
                viewModel.deleteTag(item)
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                listAdapter.notifyItemChanged(position)
                dialog.dismiss()
            }
            .show()
    }

    override fun handleSearchQuery(query: String) {
        viewModel.search(query)
    }

    private fun showEditTagDialog(title: String, tag: String) {
        val editText = EditText(requireContext()).apply {
            setText(tag)
            setPadding(40, 40, 40, 40)
        }
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setView(editText)
            .setPositiveButton(getString(R.string.button_save)) { _, _ ->
                val newTag = editText.text.toString()
                if (newTag.isNotBlank()) {
                    viewModel.saveTag(tag, newTag)
                }
            }
            .show()
    }
}