package com.AgroberriesMX.transportesagroberries.ui.shared

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor() : ViewModel() {
    val selectedVehicleCode = MutableLiveData<Int>()
    val selectedRouteCode = MutableLiveData<Int>()
    val driverName = MutableLiveData<String>()
    val selectedRouteCost = MutableLiveData<Double>() // Nuevo LiveData para el costo

}