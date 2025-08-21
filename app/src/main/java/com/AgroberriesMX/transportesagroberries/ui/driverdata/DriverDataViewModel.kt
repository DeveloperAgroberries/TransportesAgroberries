package com.AgroberriesMX.transportesagroberries.ui.driverdata

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.AgroberriesMX.transportesagroberries.domain.usecase.RouteUseCase
import com.AgroberriesMX.transportesagroberries.domain.usecase.VehicleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DriverDataViewModel @Inject constructor(
    private val vehicleUseCase: VehicleUseCase,
    private val routeUseCase: RouteUseCase,
): ViewModel(){
    //Variables para el manejo de estados
    private var _state = MutableStateFlow<DriverState>(DriverState.Loading)
    val state: StateFlow<DriverState> = _state

    //Funcion para obtener las placas
    fun getPlacasData() {
        viewModelScope.launch {
            try {
                _state.value = DriverState.Loading
                val result = withContext(Dispatchers.IO) { vehicleUseCase() }
                if (result != null) {
                    _state.value = DriverState.SuccessPlacas(
                        result
                    )
                } else {
                    _state.value = DriverState.Error("No se encontraron placas")
                }
            } catch (e: Exception) {
                _state.value =
                    DriverState.Error("Error al obtener placas:${e.message}")
            }
        }
    }
    //Funcion para obtener las placas
    fun getRutasData() {
        viewModelScope.launch {
            try {
                _state.value = DriverState.Loading
                val result = withContext(Dispatchers.IO) { routeUseCase() }
                if (result != null) {
                    _state.value = DriverState.SuccessRutas(
                        result
                    )
                } else {
                    _state.value = DriverState.Error("No se encontraron rutas")
                }
            } catch (e: Exception) {
                _state.value =
                    DriverState.Error("Error al obtener rutas:${e.message}")
            }
        }
    }
}