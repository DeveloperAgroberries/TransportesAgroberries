package com.AgroberriesMX.transportesagroberries.data

import android.util.Log
import com.AgroberriesMX.transportesagroberries.data.network.TransportApiService
import com.AgroberriesMX.transportesagroberries.data.network.request.LoginRequest
import com.AgroberriesMX.transportesagroberries.domain.Repository
import com.AgroberriesMX.transportesagroberries.domain.model.TokenModel
import javax.inject.Inject

class RepositoryImpl @Inject constructor(private val apiService: TransportApiService) : Repository {
    override suspend fun getToken(loginRequest: LoginRequest): TokenModel? {
        runCatching { apiService.login(loginRequest) }
            .onSuccess { return it.toDomain() }
            .onFailure { Log.i("TransportesApp", "Ha ocurrido un error ${it.message}") }

        return null
    }
}