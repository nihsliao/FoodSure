package com.example.foodsure.ui.foodeditform

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.example.foodsure.data.FoodItemKeys
import com.example.foodsure.data.FoodRepository
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FoodEditFormViewModel : ViewModel() {
    private companion object

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private val _navigateToHome = MutableLiveData<Boolean>()
    val navigateToHome: LiveData<Boolean> = _navigateToHome

    private val _expirationDate = MutableLiveData<Date>()
    val expirationDateLong: Long
        get() = _expirationDate.value?.time ?: MaterialDatePicker.todayInUtcMilliseconds()
    val expirationDateString: LiveData<String> = _expirationDate.map {
        dateFormat.format(it)
    }

    private val _foodItem = MutableLiveData<Map<String, String>>()
    val foodItem: LiveData<Map<String, String>> = _foodItem

    fun setExpirationDate(date: Date) {
        _expirationDate.value = date
    }


    fun onDoneNavigation() {
        _navigateToHome.value = false
    }

    fun getTags(): LiveData<List<String>> {
        return FoodRepository.getTags()
    }

    fun loadFoodItem(id: String) {
        val item = FoodRepository.getItem(id)
        if (item == null) {
            _navigateToHome.value = true
            return
        }
        _foodItem.value = item

        item[FoodItemKeys.EXPIRED]?.let { _expirationDate.value = dateFormat.parse(it) }
    }

    fun saveFoodItem(
        name: String,
        category: String,
        quantity: String,
        storage: String,
        tags: String
    ) {
        val currentId = _foodItem.value?.get(FoodItemKeys.ID)

        val newFoodItem = mapOf(
            FoodItemKeys.ID to (currentId ?: System.currentTimeMillis().toString()),
            FoodItemKeys.NAME to name,
            FoodItemKeys.CATEGORY to category,
            FoodItemKeys.EXPIRED to dateFormat.format(_expirationDate.value ?: Date(0)),
            FoodItemKeys.QUANTITY to quantity,
            FoodItemKeys.STORAGE to storage,
            FoodItemKeys.TAGS to tags
        )

        FoodRepository.saveItem(newFoodItem)
        _navigateToHome.value = true
    }
}
