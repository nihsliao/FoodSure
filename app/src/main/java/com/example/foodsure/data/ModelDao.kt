package com.example.foodsure.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ModelDao {

    /** Food Item Operations */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFoodItem(foodItem: FoodItem): Long

    @Update
    suspend fun updateFoodItem(foodItem: FoodItem)

    @Transaction
    @Query("SELECT * FROM FoodItem ORDER BY FoodItem.expiration ASC")
    fun getAllFoodItem(): Flow<List<FoodItemWithTags>>

    @Transaction
    @Query("SELECT * FROM FoodItem WHERE id = :id")
    suspend fun getFoodItemById(id: Long): FoodItemWithTags?

    @Query("SELECT DISTINCT name FROM FoodItem")
    fun getAllFoodItemName(): Flow<List<String>>

    @Query("SELECT DISTINCT category FROM FoodItem")
    fun getAllFoodItemCategory(): Flow<List<String>>

    @Query("SELECT DISTINCT storage FROM FoodItem")
    fun getAllFoodItemStorage(): Flow<List<String>>

    @Delete
    suspend fun deleteFoodItem(foodItem: FoodItem)

    @Query("DELETE FROM FoodItem WHERE id = :id")
    suspend fun deleteFoodItemById(id: Long)

    /** Food Tag Operations */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFoodTag(foodTag: FoodTag): Long

    @Update
    suspend fun updateFoodTag(foodTag: FoodTag)

    @Query("SELECT * FROM FoodTag")
    fun getAllFoodTag(): Flow<List<FoodTag>>

    @Query("SELECT DISTINCT name FROM FoodTag")
    fun getAllFoodTagName(): Flow<List<String>>

    @Query("SELECT * FROM FoodTag WHERE id = :id")
    suspend fun getTagById(id: Long): FoodTag?

    @Query("SELECT id FROM FoodTag WHERE name = :name")
    suspend fun getFoodTagIdByName(name: String): Long?

    @Query("SELECT * FROM FoodTag WHERE name = :name")
    suspend fun getFoodTagByName(name: String): FoodTag?

    @Delete
    suspend fun deleteFoodTag(foodTag: FoodTag)

    @Query("DELETE FROM FoodTag WHERE name = :tagName")
    suspend fun deleteFoodTagByName(tagName: String)

    /** Food Item Tag Cross Reference Operations */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFoodItemTagCrossRef(foodItemTagCrossRef: FoodItemTagCrossRef)

    @Query("DELETE FROM FoodItemTagCrossRef WHERE foodItemId = :foodItemId")
    suspend fun deleteFoodItemTagCrossRefById(foodItemId: Long)

    /** Transaction Operations */
    private suspend fun updateFoodItemTagCrossRef(foodItemId: Long, foodTags: List<String>) {
        deleteFoodItemTagCrossRefById(foodItemId)
        foodTags.forEach {
            var tagId = getFoodTagIdByName(it)
            if (tagId == null) {
                tagId = insertFoodTag(FoodTag(name = it))
            }
            if (tagId != -1L)
                insertFoodItemTagCrossRef(FoodItemTagCrossRef(foodItemId, tagId))
        }
    }

    @Transaction
    suspend fun insertFoodItemWithTags(foodItem: FoodItem, foodTags: List<String>) {
        val newId = insertFoodItem(foodItem)
        if (newId != -1L) updateFoodItemTagCrossRef(newId, foodTags)
    }

    @Transaction
    suspend fun updateFoodItemWithTags(foodItem: FoodItem, foodTags: List<String>) {
        updateFoodItem(foodItem)
        updateFoodItemTagCrossRef(foodItem.id, foodTags)
    }

    @Transaction
    suspend fun deleteFoodItemWithTags(id: Long) {
        deleteFoodItemById(id)
        deleteFoodItemTagCrossRefById(id)
    }

    /** Search Operations */
    @Transaction
    @Query(
        """SELECT * FROM FoodItem
            WHERE FoodItem.name LIKE '%' || :query || '%'
            OR FoodItem.category LIKE '%' || :query || '%'
            OR FoodItem.storage LIKE '%' || :query || '%'
            ORDER BY FoodItem.expiration ASC
            """
    )
    fun searchFoodItemByItemStringData(query: String): Flow<List<FoodItemWithTags>>

    @Transaction
    @Query(
        """SELECT * FROM FoodItem
            WHERE name LIKE '%' || :query || '%'
            OR category LIKE '%' || :query || '%'
            OR storage LIKE '%' || :query || '%'
            OR id IN (
                SELECT foodItemId FROM FoodItemTagCrossRef
                INNER JOIN FoodTag ON FoodItemTagCrossRef.foodTagId = FoodTag.id
                WHERE FoodTag.name LIKE '%' || :query || '%')
            ORDER BY FoodItem.expiration ASC
            """
    )
    fun searchFoodItemByAllStringData(query: String): Flow<List<FoodItemWithTags>>

    @Query("SELECT * FROM FoodTag WHERE name LIKE '%' || :query || '%'")
    fun searchFoodTag(query: String): Flow<List<FoodTag>>
}