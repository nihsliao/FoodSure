package com.example.foodsure.ui.tag

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.foodsure.data.FoodTag
import com.example.foodsure.data.ModelRepository
import com.example.foodsure.ui.BaseViewModel
import kotlinx.coroutines.launch

class TagViewModel(r: ModelRepository) : BaseViewModel(r) {
    private val searchQuery = MutableLiveData<String>("")
    val tagList: LiveData<List<FoodTag>> = searchQuery.switchMap { query ->
        repository.searchTag(query).asLiveData()
    }

    fun saveTag(oldTagName: String, newTagName: String) = viewModelScope.launch {
        if (newTagName.isEmpty() || oldTagName == newTagName) return@launch
        val newTag = repository.getTagByName(newTagName)
        // Ignore if newTag already exist
        if (newTag != null) return@launch
        else {
            // Add mode
            if (oldTagName.isBlank()) repository.insertTag(FoodTag(name = newTagName))
            else {
                // Edit mode
                val oldTag = repository.getTagByName(oldTagName)
                if (oldTag != null) repository.updateTag(oldTag.copy(name = newTagName))
            }
        }
    }

    fun deleteTag(tag: FoodTag) = viewModelScope.launch {
        repository.deleteTag(tag)
    }

    fun search(query: String) {
        searchQuery.value = query
    }
}