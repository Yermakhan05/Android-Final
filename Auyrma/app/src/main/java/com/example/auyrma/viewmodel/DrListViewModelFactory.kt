package com.example.auyrma.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.auyrma.model.datasource.ApiSource

class DrListViewModelFactory(
    private val repository: ApiSource
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DrListViewModel::class.java)) {
            return DrListViewModel(client = ApiSource.client,) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
