package com.AgroberriesMX.transportesagroberries.data.network

import com.AgroberriesMX.transportesagroberries.data.network.request.LoginRequest
import com.AgroberriesMX.transportesagroberries.data.network.response.LoginResponse
import com.AgroberriesMX.transportesagroberries.data.network.response.LoginsResponse
import com.AgroberriesMX.transportesagroberries.data.network.response.RouteResponseWrapper
import com.AgroberriesMX.transportesagroberries.data.network.response.VehiclesResponseWrapper
import com.AgroberriesMX.transportesagroberries.data.network.response.WorkerResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface TransportApiService {
    @POST("LoginUser")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse

    @GET("ListLogins")
    suspend fun loginsData(@Header("Authorization") token: String): LoginsResponse

    @GET("ListRoutes")
    /*suspend fun routes(@Header("Authorization") token: String): RouteResponse*/
    suspend fun routes(): RouteResponseWrapper

    @GET("ListPlates")
    //suspend fun vehicles(@Header("Authorization") token: String): VehicleResponse
    suspend fun vehicles(): VehiclesResponseWrapper

    @GET("ListWorkers")
    suspend fun workers(@Header("Authorization") token: String): WorkerResponse
}