package com.example.foodsure.ui.foodeditform

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter

class TagArrayAdapter(
    context: Context,
    resource: Int,
    items: MutableList<String>,
    private val predicate: (String) -> Boolean
) : ArrayAdapter<String>(context, resource, items) {
    private val _lock = Any()
    private val _originList = items.toMutableList()
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults? {
                val query = constraint?.toString()?.trim() ?: ""

                val values: MutableList<String>
                synchronized(_lock) {
                    values = _originList.toMutableList()
                }

                val filteredList = if (query.isNotBlank()) {
                    val tempList = values.filter { predicate(it) }.toMutableList()
                    if (!tempList.contains(query)) {
                        // Add user input on the first
                        tempList.add(0, query)
                    }
                    tempList
                } else {
                    values
                }

                return FilterResults().apply {
                    this.values = filteredList
                    this.count = filteredList.size
                }
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(
                constraint: CharSequence?,
                results: FilterResults?
            ) {
                clear()
                results?.let { addAll(it.values as List<String>) }
            }
        }
    }

    fun updateItemList(newItems: List<String>) {
        synchronized(_lock) {
            _originList.clear()
            _originList.addAll(newItems)
        }
        notifyDataSetChanged()
    }
}