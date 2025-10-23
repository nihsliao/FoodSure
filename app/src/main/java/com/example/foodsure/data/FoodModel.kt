package com.example.foodsure.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

class FoodModel {

    @Entity
    data class FoodItem (
        @PrimaryKey(autoGenerate = true)
        val id: Int = 0,
        val name: String = "",
        val category: String = "",
        val expiration: Date = Date(),
        val quantity: Int = 0,
        val storage: String = ""
    )

    @Entity(indices = [Index(value = ["name"], unique = true)])
    data class FoodTag (
        @PrimaryKey(autoGenerate = true)
        val id: Int = 0,
        val name: String = ""
    )

    @Entity(primaryKeys = ["foodItemId", "foodTagId"])
    data class FoodItemTagCrossRef (
        val foodItemId: Int = 0,
        val foodTagId: Int = 0
    )
}