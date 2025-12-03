package com.AgroberriesMX.transportesagroberries.ui.shared

import androidx.lifecycle.SavedStateHandle // 👈 Importar SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val state: SavedStateHandle // 👈 Inyectar SavedStateHandle
) : ViewModel() {

    // Usar LiveData con SavedStateHandle para persistir el valor.
    // El 0 es el valor por defecto si no existe en el SavedStateHandle.
    val selectedVehicleCode = state.getLiveData<Int>("vehicle_code", 0)

    // El 0 es el valor por defecto si no existe en el SavedStateHandle.
    val selectedRouteCode = state.getLiveData<Int>("route_code", 0)

    // "Sin Nombre" es el valor por defecto.
    val driverName = state.getLiveData<String>("driver_name", "Sin Nombre")

    // 0.0 es el valor por defecto.
    val selectedRouteCost = state.getLiveData<Double>("route_cost", 0.0)

}