package com.example.foodsure.ui.tag

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import com.example.foodsure.data.FoodRepository

class TagViewModel : ViewModel() {
    private val searchQuery = MutableLiveData<String>("")
    val tagList: LiveData<List<String>> = searchQuery.switchMap { query ->
        FoodRepository.searchTag(query).asLiveData()
    }

    fun saveTag(oldTag: String, newTag: String) {
        FoodRepository.editTag(oldTag, newTag)
    }

    fun deleteTag(tag: String) {
        FoodRepository.deleteTag(tag)
    }

    fun search(query: String) {
        searchQuery.value = query
    }
}