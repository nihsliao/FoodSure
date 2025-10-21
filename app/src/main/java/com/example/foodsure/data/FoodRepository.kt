package com.example.foodsure.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object FoodItemKeys {
    const val ID = "id"
    const val NAME = "name"
    const val CATEGORY = "category"
    const val EXPIRED = "expired"
    const val QUANTITY = "quantity"
    const val STORAGE = "storage"
    const val TAGS = "tags"
} // Temporary solution since we don't want to invoke data yet


object FoodRepository {
    private val _foodList = MutableLiveData<List<Map<String, String>>>()
    private val _tagList = MutableLiveData<List<String>>()

    init {
        // Temporary fake data
        val initialFoodList = listOf(
            mapOf(
                FoodItemKeys.ID to "0",
                FoodItemKeys.NAME to "Fish",
                FoodItemKeys.CATEGORY to "meat",
                FoodItemKeys.EXPIRED to "2025-09-22",
                FoodItemKeys.QUANTITY to "5",
                FoodItemKeys.STORAGE to "Fridge",
                FoodItemKeys.TAGS to "Costco, Local"
            ),
            mapOf(
                FoodItemKeys.ID to "1",
                FoodItemKeys.NAME to "Chicken",
                FoodItemKeys.CATEGORY to "meat",
                FoodItemKeys.EXPIRED to "2025-12-22",
                FoodItemKeys.QUANTITY to "3",
                FoodItemKeys.STORAGE to "Fridge",
                FoodItemKeys.TAGS to "Costco, Red"
            ),
            mapOf(
                FoodItemKeys.ID to "2",
                FoodItemKeys.NAME to "Beef",
                FoodItemKeys.CATEGORY to "meat",
                FoodItemKeys.EXPIRED to "2026-09-22",
                FoodItemKeys.QUANTITY to "7",
                FoodItemKeys.STORAGE to "Fridge",
                FoodItemKeys.TAGS to "IKEA, Red"
            ),
            mapOf(
                FoodItemKeys.ID to "3",
                FoodItemKeys.NAME to "Cookies",
                FoodItemKeys.CATEGORY to "snack",
                FoodItemKeys.EXPIRED to "2026-09-20",
                FoodItemKeys.QUANTITY to "9",
                FoodItemKeys.STORAGE to "box",
                FoodItemKeys.TAGS to "IKEA, Red"
            ),
            mapOf(
                FoodItemKeys.ID to "4",
                FoodItemKeys.NAME to "Chicken",
                FoodItemKeys.CATEGORY to "meat",
                FoodItemKeys.EXPIRED to "2026-09-22",
                FoodItemKeys.QUANTITY to "1",
                FoodItemKeys.STORAGE to "Fridge",
                FoodItemKeys.TAGS to "Costco, Red"
            ),
            mapOf(
                FoodItemKeys.ID to "5",
                FoodItemKeys.NAME to "Cookies",
                FoodItemKeys.CATEGORY to "snack",
                FoodItemKeys.EXPIRED to "2025-11-22",
                FoodItemKeys.QUANTITY to "8",
                FoodItemKeys.STORAGE to "box",
                FoodItemKeys.TAGS to "IKEA, Blue"
            ),
        )
        _foodList.value = initialFoodList

        val initialTagList = listOf("IKEA", "BLUE", "Costco", "Red", "Local")
        _tagList.value = initialTagList
    }

    /* Item READ, WRITE/UPDATE, DELETE, SEARCH START */
    fun getItem(id: String): Map<String, String>? {
        val index = _foodList.value?.indexOfFirst { it[FoodItemKeys.ID] == id } ?: -1
        if (index == -1) return null
        return _foodList.value?.get(index)
    }

    fun saveItem(item: Map<String, String>) {
        val currentList = _foodList.value?.toMutableList() ?: mutableListOf()
        val id = item[FoodItemKeys.ID]

        val existingItemIndex =
            if (id == null) -1 else currentList.indexOfFirst { it[FoodItemKeys.ID] == id }

        if (existingItemIndex != -1) {
            // Update existing item
            currentList[existingItemIndex] = item
        } else {
            // Add as new item with a new ID
            val newItem = item.toMutableMap()
            newItem[FoodItemKeys.ID] = System.currentTimeMillis().toString()
            currentList.add(newItem)
        }
        _foodList.value = currentList
    }

    fun removeItem(item: Map<String, String>) {
        val currentList = _foodList.value?.toMutableList() ?: mutableListOf()
        currentList.removeIf { it[FoodItemKeys.ID] == item[FoodItemKeys.ID] }
        _foodList.value = currentList
    }

    fun searchItem(query: String): Flow<List<Map<String, String>>> {
        return _foodList.asFlow().map {
            if (query.isBlank()) it
            else {
                it.filter { item ->
                    item[FoodItemKeys.NAME]?.contains(query, ignoreCase = true) == true
                            || item[FoodItemKeys.TAGS]?.contains(query, ignoreCase = true) == true
                            || item[FoodItemKeys.CATEGORY]?.contains(
                        query,
                        ignoreCase = true
                    ) == true
                }
            }
        }
    }
    /* Item related END */

    /* Tag READ, WRITE/UPDATE, DELETE, SEARCH START */
    /* Tag related END */
    fun getTags(): LiveData<List<String>> {
        return _tagList
    }

    fun editTag(oldTag: String, newTag: String) {
        if (newTag.isEmpty() || oldTag == newTag) return
        updateTags { currentList ->
            if (currentList.contains(newTag)) return@updateTags
            val index = currentList.indexOf(oldTag)
            if (index != -1) {
                currentList[index] = newTag
            } else {
                currentList.add(newTag)
            }
        }
    }

    fun deleteTag(tag: String) {
        updateTags { currentList ->
            currentList.remove(tag)
        }
    }

    private fun updateTags(action: (MutableList<String>) -> Unit) {
        val currentList = _tagList.value?.toMutableList() ?: mutableListOf()
        action(currentList)
        _tagList.value = currentList
    }

    fun searchTag(query: String): Flow<List<String>> {
        return _tagList.asFlow().map {
            if (query.isBlank()) it
            else {
                it.filter { item ->
                    item.contains(query, ignoreCase = true)
                }
            }
        }
    }
}