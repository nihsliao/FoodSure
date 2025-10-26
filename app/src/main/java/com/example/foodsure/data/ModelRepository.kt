package com.example.foodsure.data

import kotlinx.coroutines.flow.Flow

interface ModelRepository {
    /** FoodItem operations */
    suspend fun insertItem(item: FoodItem, tags: List<String>)

    suspend fun updateItem(item: FoodItem, tags: List<String>)

    suspend fun getItem(id: Long): FoodItemWithTags?

    suspend fun deleteItem(foodItem: FoodItem)

    suspend fun deleteItem(foodItemId: Long)

    fun searchItem(query: String): Flow<List<FoodItemWithTags>>


    /** FoodTags operations */
    suspend fun insertTag(tag: FoodTag)

    suspend fun updateTag(tag: FoodTag)

    suspend fun deleteTag(tag: FoodTag)

    suspend fun getTagByName(name: String): FoodTag?

    fun getAllTagsName(): Flow<List<String>>

    fun searchTag(query: String): Flow<List<FoodTag>>
}