package com.example.foodsure.data

import de.raphaelebner.roomdatabasebackup.core.RoomBackup
import kotlinx.coroutines.flow.Flow

interface ModelRepository {
    enum class RespondStatus {
        SUCCESS,
        FAIL_ON_BACKUP,
        FAIL_ON_RESTORE,
        FAIL_ON_UPLOAD,
        FAIL_ON_DELETE,
        FAIL_ON_DOWNLOAD,
        TIMEOUT
    }

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

    suspend fun uploadBackup(backup: RoomBackup): RespondStatus

    suspend fun listBackup()

    suspend fun downloadBackup(backup: RoomBackup): RespondStatus

    suspend fun deleteBackup(): RespondStatus
}