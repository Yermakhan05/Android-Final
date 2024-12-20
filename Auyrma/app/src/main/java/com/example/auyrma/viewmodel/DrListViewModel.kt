package com.example.auyrma.viewmodel

import DrListUI
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.auyrma.model.api.DrApi
import com.example.auyrma.model.entity.Dr
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DrListViewModel(
    private val client: DrApi
) : ViewModel() {

    private val _drListUI = MutableLiveData<DrListUI>()
    val drListUI: LiveData<DrListUI> get() = _drListUI

    private val _averagePrice = MutableLiveData<Int>()
    val averagePrice: LiveData<Int> get() = _averagePrice

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun fetchDoctorList(city: String, category: String) {
        _drListUI.value = DrListUI.Loading(true)

        val params = mutableMapOf<String, String>()
        params["city"] = city
        if (category != "All") params["search"] = category
        viewModelScope.launch(Dispatchers.IO) {

            val movieListDeferred = async {
                client.fetchMedicsWithParams(params)
            }
            val drList = movieListDeferred.await()

            withContext(Dispatchers.Main) {

                if (drList.results.isEmpty()) {
                    _drListUI.value = DrListUI.Empty
                } else {
                    calculateAveragePrice(drList.results)
                    drList.results.forEach {
                        it.isFavorite = it.favorites.contains(2)
                    }
                    _drListUI.value = DrListUI.Success(drList.results)
                }

                _drListUI.value = DrListUI.Loading(false)
            }
        }
    }

    private fun calculateAveragePrice(drList: List<Dr>) {
        val avgPrice = if (drList.isNotEmpty()) drList.sumOf { it.price } / drList.size else 0
        _averagePrice.postValue(avgPrice)
    }

}