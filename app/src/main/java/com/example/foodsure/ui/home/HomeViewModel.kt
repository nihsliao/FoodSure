package com.example.foodsure.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.foodsure.data.FoodItemWithTags
import com.example.foodsure.data.ModelRepository
import com.example.foodsure.ui.BaseViewModel
import kotlinx.coroutines.launch

class HomeViewModel(repository: ModelRepository) : BaseViewModel(repository) {
    private val searchQuery = MutableLiveData<String>("")
    val foodList: LiveData<List<FoodItemWithTags>> = searchQuery.switchMap { query ->
        repository.searchItem(query).asLiveData()
    }

    fun removeItem(foodItemWithTags: FoodItemWithTags) {
        viewModelScope.launch {
            repository.deleteItem(foodItemWithTags.foodItem)

        }
    }

    fun search(query: String) {
        searchQuery.value = query
    }
}
