package com.AgroberriesMX.transportesagroberries.ui.driverdata

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.AgroberriesMX.transportesagroberries.domain.usecase.RouteUseCase
import com.AgroberriesMX.transportesagroberries.domain.usecase.VehicleUseCase
import com.AgroberriesMX.transportesagroberries.domain.usecase.WorkerUseCase
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
    private val workerUseCase: WorkerUseCase // 2. Inyecta el WorkerUseCase
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
    //Funcion para obtener las rutas
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

    // 3. Agrega la función para obtener a los trabajadores
    fun getWorkersData() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) { workerUseCase() }
                // No necesitas manejar el estado aquí si solo quieres que se guarden en la BD.
            } catch (e: Exception) {
                // Puedes agregar un log para depuración si la llamada falla
                 Log.e("DriverDataViewModel", "Error al obtener trabajadores: ${e.message}")
            }
        }
    }
}