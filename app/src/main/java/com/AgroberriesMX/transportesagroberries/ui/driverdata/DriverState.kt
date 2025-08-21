package com.AgroberriesMX.transportesagroberries.ui.driverdata

import com.AgroberriesMX.transportesagroberries.domain.model.RouteModel
import com.AgroberriesMX.transportesagroberries.domain.model.VehicleModel

sealed class DriverState {
    data object Loading : DriverState()

    data class  Error(val error: String) : DriverState()
    data class  SuccessPlacas(val successPlacas: List<VehicleModel>) : DriverState()
    data class  SuccessRutas(val successRutas: List<RouteModel>) : DriverState()
}