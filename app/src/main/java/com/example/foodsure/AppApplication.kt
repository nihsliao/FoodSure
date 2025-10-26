package com.example.foodsure

import android.app.Application
import com.example.foodsure.data.AppDatabase
import com.example.foodsure.data.ModelRepository
import com.example.foodsure.data.RoomRepository

class AppApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository: ModelRepository by lazy {
        RoomRepository(database, applicationContext)
        /* applicationContext.resources.getBoolean(R.bool.config_use_room_database) could be used
         to determine which repository to use */
    }
}