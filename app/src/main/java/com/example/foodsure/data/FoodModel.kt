package com.example.foodsure.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import androidx.room.TypeConverter
import java.util.Date

@Entity
data class FoodItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String = "",
    val category: String = "",
    val expiration: Date = Date(),
    val quantity: Double = 0.0,
    val storage: String = ""
)

@Entity(indices = [Index(value = ["name"], unique = true)])
data class FoodTag(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String = ""
)

@Entity(
    primaryKeys = ["foodItemId", "foodTagId"],
    foreignKeys = [
        ForeignKey(
            entity = FoodItem::class,
            parentColumns = ["id"],
            childColumns = ["foodItemId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = FoodTag::class,
            parentColumns = ["id"],
            childColumns = ["foodTagId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class FoodItemTagCrossRef(
    val foodItemId: Long = 0,
    val foodTagId: Long = 0
)

data class FoodItemWithTags(
    @Embedded val foodItem: FoodItem,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            FoodItemTagCrossRef::class,
            "foodItemId", "foodTagId"
        )
    )
    val tags: List<FoodTag>
)

data class TagsWithFoodItems(
    @Embedded val foodTag: FoodTag,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(FoodItemTagCrossRef::class)
    )
    val foodItems: List<FoodItem>
)

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}
