package com.example.auyrma.viewmodel

import androidx.lifecycle.*
import com.example.auyrma.model.datasource.ApiSource
import com.example.auyrma.model.entity.Hospital
import com.example.auyrma.model.entity.Pharmacy
import kotlinx.coroutines.launch

class HospitalListViewModel : ViewModel() {
    private val _hospitalList = MutableLiveData<List<Hospital>>()
    val hospitalList: LiveData<List<Hospital>> = _hospitalList

    private val _pharmacyList = MutableLiveData<List<Pharmacy>>()
    val pharmacyList: LiveData<List<Pharmacy>> = _pharmacyList

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private var selectedCity: String = "Almaty"

    fun setCity(city: String) {
        selectedCity = city
    }

    fun fetchHospitals(params: Map<String, String?>) {
        viewModelScope.launch {
            try {
                val updatedParams = params + ("city" to selectedCity)
                val hospitals = ApiSource.client.fetchHospitals(updatedParams)
                _hospitalList.postValue(hospitals)
            } catch (e: Exception) {
                _errorMessage.postValue("Error fetching hospitals: ${e.message}")
            }
        }
    }

    fun fetchPharmacies(params: Map<String, String?>) {
        viewModelScope.launch {
            try {
                val updatedParams = params + ("city" to selectedCity)
                val pharmacies = ApiSource.client.fetchPharmacies(updatedParams)
                _pharmacyList.postValue(pharmacies)
            } catch (e: Exception) {
                _errorMessage.postValue("Error fetching pharmacies: ${e.message}")
            }
        }
    }
}



