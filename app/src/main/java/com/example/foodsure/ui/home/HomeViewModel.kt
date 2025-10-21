package com.example.foodsure.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import com.example.foodsure.data.FoodRepository

class HomeViewModel : ViewModel() {
    private val searchQuery = MutableLiveData<String>("")
    val foodList: LiveData<List<Map<String, String>>> = searchQuery.switchMap { query ->
        FoodRepository.searchItem(query).asLiveData()
    }

    fun removeItem(item: Map<String, String>) {
        FoodRepository.removeItem(item)
    }

    fun search(query: String) {
        searchQuery.value = query
    }
}
