package com.example.foodsure.ui.foodeditform

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.foodsure.data.FoodItem
import com.example.foodsure.data.FoodItemWithTags
import com.example.foodsure.data.ModelRepository
import com.example.foodsure.ui.BaseViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FoodEditFormViewModel(r: ModelRepository) : BaseViewModel(r) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private val _navigateToHome = MutableLiveData<Boolean>()
    val navigateToHome: LiveData<Boolean> = _navigateToHome

    private val _expirationDate = MutableLiveData<Date>()
    val expirationDate: LiveData<Date> = _expirationDate
    val expirationDateString: LiveData<String> = _expirationDate.map {
        dateFormat.format(it)
    }

    private val _toastMsg = MutableLiveData("")
    val toastMsg: LiveData<String> = _toastMsg

    private val _foodItem by lazy { MutableLiveData<FoodItemWithTags>() }
    val foodItem: LiveData<FoodItemWithTags> = _foodItem

    val allTags = repository.getAllTagsName().asLiveData()
    fun setExpirationDate(date: Date) {
        _expirationDate.value = date
    }

    fun setToast(string: String) {
        _toastMsg.value = string
    }

    fun clearFormState() {
        _foodItem.value = null
        _expirationDate.value = Date()
    }

    fun onDoneNavigation() {
        _navigateToHome.value = false
        _foodItem.value = null
    }

    fun loadFoodItem(id: Long) = viewModelScope.launch {
        val item = repository.getItem(id)
        if (item == null) {
            _navigateToHome.value = true
            return@launch
        }
        _foodItem.value = item
        _expirationDate.value = item.foodItem.expiration
    }

    fun saveFoodItem(
        id: Long?,
        name: String,
        category: String,
        expiration: Date,
        quantity: Double,
        storage: String,
        tags: List<String>
    ) {
        if (name.isBlank() || category.isBlank() || quantity <= 0 || storage.isBlank()) {
            _toastMsg.value = "Name, Category, Quantity and Storage are required fields."
            return
        }

        viewModelScope.launch {
            val foodItem = FoodItem(
                name = name,
                category = category,
                expiration = expiration,
                quantity = quantity,
                storage = storage,
            )
            if (id == null) {
                // Add Mode
                repository.insertItem(foodItem, tags)
            } else {
                // Edit Mode
                repository.updateItem(foodItem.copy(id = id), tags)
            }
        }
        _navigateToHome.value = true
    }

    fun onDoneToast() {
        _toastMsg.value = ""
    }
}
