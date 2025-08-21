package com.AgroberriesMX.transportesagroberries.ui.privacypolicy

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.AgroberriesMX.transportesagroberries.domain.model.LoginModel
import com.AgroberriesMX.transportesagroberries.domain.model.RouteModel
import com.AgroberriesMX.transportesagroberries.domain.model.VehicleModel
import com.AgroberriesMX.transportesagroberries.domain.model.WorkerModel
import com.AgroberriesMX.transportesagroberries.domain.usecase.LoginsUseCase
import com.AgroberriesMX.transportesagroberries.domain.usecase.RouteUseCase
import com.AgroberriesMX.transportesagroberries.domain.usecase.VehicleUseCase
import com.AgroberriesMX.transportesagroberries.domain.usecase.WorkerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrivacyPolicyViewModel @Inject constructor(
    private val getLoginsUseCase: LoginsUseCase,
    private val getRouteUseCase: RouteUseCase,
    private val getVehicleUseCase: VehicleUseCase,
    private val getWorkerUseCase: WorkerUseCase
) : ViewModel() {
    private var _state = MutableLiveData<PrivacyPolicyState>()
    val state: LiveData<PrivacyPolicyState> = _state

    lateinit var loginModel: LoginModel
    //lateinit var routeModel: RouteModel
    var routeList: List<RouteModel> = emptyList()
    //lateinit var vehicleModel: VehicleModel
    var vehicleList: List<VehicleModel> = emptyList()
    lateinit var workerModel: WorkerModel

    fun dataResponse(token: String) {
        viewModelScope.launch {
            _state.value = PrivacyPolicyState.Loading
            try {
                val responseLogins = getLoginsUseCase(token)
                val responseRoutes = getRouteUseCase()
                val responseVehicles = getVehicleUseCase()
                val responseWorkers = getWorkerUseCase(token)

                if (responseLogins != null && responseRoutes != null && responseVehicles != null && responseWorkers != null) {
                    loginModel = responseLogins
                    //routeList = responseRoutes
                    routeList = responseRoutes ?: emptyList()
                    //vehicleList = responseVehicles
                    vehicleList = responseVehicles ?: emptyList()
                    workerModel = responseWorkers

                    _state.value = PrivacyPolicyState.Success(loginModel, routeList, vehicleList, workerModel)
                } else {
                    _state.value = PrivacyPolicyState.Error("Alguno de los datos fallo en la descarga, vuelve a intentarlo.")
                }
            } catch (e: Exception) {
                _state.value = PrivacyPolicyState.Error(e.message ?: "A ocurrido un error")
            }
        }
    }
}