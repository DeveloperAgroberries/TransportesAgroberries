package com.AgroberriesMX.transportesagroberries.data.network

import com.AgroberriesMX.transportesagroberries.data.network.request.LoginRequest
import com.AgroberriesMX.transportesagroberries.data.network.request.UploadResponse
import com.AgroberriesMX.transportesagroberries.data.network.response.LoginResponse
import com.AgroberriesMX.transportesagroberries.data.network.response.LoginsResponse
import com.AgroberriesMX.transportesagroberries.data.network.response.RouteResponseWrapper
import com.AgroberriesMX.transportesagroberries.data.network.response.VehiclesResponseWrapper
import com.AgroberriesMX.transportesagroberries.data.network.response.WorkersResponseWrapper
import com.AgroberriesMX.transportesagroberries.domain.model.FormattedRecordsModel
import retrofit2.Response
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
    suspend fun routes(): RouteResponseWrapper

    @GET("ListPlates")
    suspend fun vehicles(): VehiclesResponseWrapper

    @GET("ListWorkers")
    suspend fun workers(): WorkersResponseWrapper

    @POST("SaveTransportDataRecords")
    suspend fun uploadData(@Body records: List<FormattedRecordsModel>): Response<UploadResponse>
}