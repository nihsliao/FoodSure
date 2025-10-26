package com.example.foodsure.data

import kotlinx.coroutines.flow.Flow

class RoomRepository(private val modelDao: ModelDao) : ModelRepository {

    /** FoodItem operations */
    override suspend fun insertItem(item: FoodItem, tags: List<String>) {
        modelDao.insertFoodItemWithTags(item, tags)
    }

    override suspend fun updateItem(item: FoodItem, tags: List<String>) {
        modelDao.updateFoodItemWithTags(item, tags)
    }

    override suspend fun getItem(id: Long): FoodItemWithTags? {
        return modelDao.getFoodItemById(id)
    }

    override suspend fun deleteItem(foodItem: FoodItem) {
        modelDao.deleteFoodItem(foodItem)
    }

    override suspend fun deleteItem(foodItemId: Long) {
        modelDao.deleteFoodItemWithTags(foodItemId)
    }

    override fun searchItem(query: String): Flow<List<FoodItemWithTags>> {
        return modelDao.searchFoodItemByAllStringData(query)
    }

    /** FoodTags operations */
    override suspend fun insertTag(tag: FoodTag) {
        modelDao.insertFoodTag(tag)
    }

    override suspend fun updateTag(tag: FoodTag) {
        modelDao.updateFoodTag(tag)
    }

    override fun getAllTagsName(): Flow<List<String>> {
        return modelDao.getAllFoodTagName()
    }

    override suspend fun deleteTag(tag: FoodTag) {
        modelDao.deleteFoodTag(tag)
    }

    override suspend fun getTagByName(name: String): FoodTag? {
        return modelDao.getFoodTagByName(name)
    }

    override fun searchTag(query: String): Flow<List<FoodTag>> {
        return modelDao.searchFoodTag(query)
    }
}