package com.AgroberriesMX.transportesagroberries.data.network

import com.AgroberriesMX.transportesagroberries.data.network.request.LoginRequest
import com.AgroberriesMX.transportesagroberries.data.network.response.LoginResponse
import com.AgroberriesMX.transportesagroberries.domain.model.LoginModel
import com.AgroberriesMX.transportesagroberries.domain.model.RouteModel
import com.AgroberriesMX.transportesagroberries.domain.model.VehicleModel
import com.AgroberriesMX.transportesagroberries.domain.model.WorkerModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface TransportApiService {
    @POST("LoginUser")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse

    @GET("ListLogins")
    suspend fun loginsData(@Header("Authorization") token: String): Call<List<LoginModel>>

    @GET("ListRoutes")
    suspend fun routes(@Header("Authorization") token: String): Call<List<RouteModel>>

    @GET("ListPlates")
    suspend fun vehicles(@Header("Authorization") token: String): Call<List<VehicleModel>>

    @GET("ListWorkers")
    suspend fun workers(@Header("Authorization") token: String): Call<List<WorkerModel>>
}