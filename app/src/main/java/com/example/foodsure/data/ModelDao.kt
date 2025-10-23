package com.example.foodsure.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ModelDao {

    /* Food Item Operations */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFoodItem(foodItem: FoodModel.FoodItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateFoodItem(foodItem: FoodModel.FoodItem)

    @Delete
    suspend fun deleteFoodItem(foodItem: FoodModel.FoodItem)
    @Query("SELECT * FROM FoodItem")
    suspend fun getAllFoodItem(): Flow<List<FoodModel.FoodItem>>

    @Query("SELECT * FROM FoodItem WHERE id = :id")
    suspend fun getFoodItemById(id: Int): Flow<FoodModel.FoodItem>

    @Query(
        "SELECT * FROM FoodItem WHERE name LIKE '%' || :query || '%'" +
                "   OR category LIKE '%' || :query || '%'" +
                "   OR storage LIKE '%' || :query || '%'" +
                "   OR id IN (" +
                "       SELECT * FROM FoodItemTagCrossRef" +
                "       WHERE foodTagId IN (" +
                "           SELECT id FROM FoodTag" +
                "           WHERE name LIKE '%' || :query || '%')" +
                ")"
    )
    suspend fun searchFoodItem(query: String): Flow<List<FoodModel.FoodItem>>

    /* Food Tag Operations */
    @Insert
    suspend fun insertFoodTag(foodTag: FoodModel.FoodTag)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateFoodTag(foodTag: FoodModel.FoodTag)

    @Delete
    suspend fun deleteFoodTag(foodTag: FoodModel.FoodTag)

    @Query("SELECT * FROM FoodTag")
    suspend fun getAllFoodTag(): Flow<List<FoodModel.FoodTag>>

    @Query("SELECT * FROM FoodTag WHERE id = :id")
    suspend fun getTagById(id: Int): Flow<FoodModel.FoodTag>

    @Query("SELECT * FROM FoodTag WHERE name LIKE '%' || :query || '%'")
    suspend fun searchFoodTag(query: String): Flow<List<FoodModel.FoodTag>>

}