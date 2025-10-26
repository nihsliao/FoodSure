package com.example.foodsure.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.foodsure.data.ModelRepository

interface IViewModel {
    val repository: ModelRepository
}

abstract class BaseViewModel(override val repository: ModelRepository) : ViewModel(), IViewModel {
    // Define ViewModel factory in a companion object
    companion object {
        fun provideFactory(
            repository: ModelRepository,
            provider: (ModelRepository) -> ViewModel
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return provider(repository) as T
            }
        }
    }
}