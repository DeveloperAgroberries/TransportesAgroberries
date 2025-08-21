package com.AgroberriesMX.transportesagroberries.ui.privacypolicy

import com.AgroberriesMX.transportesagroberries.domain.model.LoginModel
import com.AgroberriesMX.transportesagroberries.domain.model.RouteModel
import com.AgroberriesMX.transportesagroberries.domain.model.VehicleModel
import com.AgroberriesMX.transportesagroberries.domain.model.WorkerModel

sealed class PrivacyPolicyState {
    data object Waiting:PrivacyPolicyState()
    data object Loading:PrivacyPolicyState()
    data class Error(val message: String):PrivacyPolicyState()
    data class Success(val logins: LoginModel, val routes: List<RouteModel>, val vehicles: List<VehicleModel>, val worker: WorkerModel):PrivacyPolicyState()
}