package com.example.foodsure.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.foodsure.R

abstract class BaseListFragment<VB : ViewBinding, T : Any, VM : ViewModel>() :
    BaseFragment<VB, VM>() {
    /**
     * The ListAdapter for the RecyclerView. Subclasses must provide their specific adapter instance.
     */
    protected abstract val listAdapter: ListAdapter<out T, *>

    /**
     * The RecyclerView instance from the binding.
     */
    protected abstract val recyclerView: RecyclerView

    /**
     * The LiveData containing the list of items to be displayed.
     * The value should be provided from the [viewModel].
     */
    protected abstract val listData: LiveData<out List<T>>

    val itemTouchHelperCallback =
        object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.END) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = listAdapter.currentList[position]
                    showDeletionDialog(position, item)
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setUpMenuHost()
    }

    private fun setupRecyclerView() {
        recyclerView.adapter = listAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        listData.observe(viewLifecycleOwner) { items ->
            (listAdapter as ListAdapter<T, *>).submitList(items)
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun setUpMenuHost() {
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main, menu)
                val searchItem = menu.findItem(R.id.action_search)
                    .setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                        override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                            menuHost.invalidateMenu()
                            return true
                        }

                        override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                            return true
                        }

                    })
                val searchView = searchItem.actionView as SearchView
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        handleSearchQuery(newText.orEmpty())
                        return true
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    abstract fun showDeletionDialog(position: Int, item: T)
    abstract fun handleSearchQuery(query: String)
}