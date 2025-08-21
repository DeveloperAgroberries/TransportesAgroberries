package com.AgroberriesMX.transportesagroberries.domain

import com.AgroberriesMX.transportesagroberries.data.network.request.LoginRequest
import com.AgroberriesMX.transportesagroberries.domain.model.LoginModel
import com.AgroberriesMX.transportesagroberries.domain.model.RouteModel
import com.AgroberriesMX.transportesagroberries.domain.model.TokenModel
import com.AgroberriesMX.transportesagroberries.domain.model.VehicleModel
import com.AgroberriesMX.transportesagroberries.domain.model.WorkerModel

interface Repository {
    suspend fun getToken(loginRequest: LoginRequest): TokenModel?
    suspend fun getLogins(token: String): LoginModel?
    //suspend fun getRoutes(token: String): RouteModel?
    suspend fun getRoutes():List<RouteModel>?
    //suspend fun getVehicles(token: String): VehicleModel?
    suspend fun getVehicles(): List<VehicleModel>?
    suspend fun getWorkers(token: String): WorkerModel?
}