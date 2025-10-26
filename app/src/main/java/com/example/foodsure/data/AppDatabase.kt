package com.example.foodsure.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(
    version = 1,
    entities = [FoodItem::class, FoodTag::class, FoodItemTagCrossRef::class],
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun foodModelDao(): ModelDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "foodModelDatabase"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance

                return instance
            }
        }
    }
}