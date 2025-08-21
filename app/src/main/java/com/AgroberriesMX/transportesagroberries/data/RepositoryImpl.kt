package com.AgroberriesMX.transportesagroberries.data

import android.util.Log
import com.AgroberriesMX.transportesagroberries.data.network.TransportApiService
import com.AgroberriesMX.transportesagroberries.data.network.request.LoginRequest
import com.AgroberriesMX.transportesagroberries.domain.Repository
import com.AgroberriesMX.transportesagroberries.domain.model.LoginModel
import com.AgroberriesMX.transportesagroberries.domain.model.RouteModel
import com.AgroberriesMX.transportesagroberries.domain.model.TokenModel
import com.AgroberriesMX.transportesagroberries.domain.model.VehicleModel
import com.AgroberriesMX.transportesagroberries.domain.model.WorkerModel
import javax.inject.Inject

class RepositoryImpl @Inject constructor(private val apiService: TransportApiService) : Repository {
    companion object{
        private const val APP_INFO_TAG_KEY = "TransportesApp"
    }
    override suspend fun getToken(loginRequest: LoginRequest): TokenModel? {
        runCatching { apiService.login(loginRequest) }
            .onSuccess { return it.toDomain() }
            .onFailure { Log.i(APP_INFO_TAG_KEY, "Ha ocurrido un error ${it.message}") }

        return null
    }

    override suspend fun getLogins(token: String): LoginModel? {
        runCatching { apiService.loginsData(token) }
            .onSuccess { return it.toDomain() }
            .onFailure { Log.i(APP_INFO_TAG_KEY, "Ha ocurrido un error ${it.message}") }

        return null
    }

    override suspend fun getRoutes(): List<RouteModel>? {
        /*runCatching { apiService.routes(token) }
            .onSuccess { return it.toDomain() }
            .onFailure { Log.i(APP_INFO_TAG_KEY, "Ha ocurrido un error ${it.message}") }

        return null*/
        return try {
            val wrapper = apiService.routes()
            wrapper.response.map { it.toDomain() }
        } catch (e: Exception) {
            Log.e("RouteRepository", "Error al obtener rutas: ${e.message}")
            emptyList()
        }
    }

    override suspend fun getVehicles(): List<VehicleModel>? {
        /*runCatching { apiService.vehicles() }
            .onSuccess { return it.map { dto -> dto.toDomain() } }
            .onFailure { Log.i(APP_INFO_TAG_KEY, "Ha ocurrido un error ${it.message}") }

        return null*/
        return try {
            val wrapper = apiService.vehicles()
            wrapper.response.map { it.toDomain() }
        } catch (e: Exception) {
            Log.e("VehicleRepository", "Error al obtener vehículos: ${e.message}")
            emptyList()
        }
    }

    override suspend fun getWorkers(token: String): WorkerModel? {
        runCatching { apiService.workers(token) }
            .onSuccess { return it.toDomain() }
            .onFailure { Log.i(APP_INFO_TAG_KEY, "Ha ocurrido un error ${it.message}") }

        return null
    }
}