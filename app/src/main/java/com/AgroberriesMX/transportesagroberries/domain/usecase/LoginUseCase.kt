package com.AgroberriesMX.transportesagroberries.domain.usecase

import com.AgroberriesMX.transportesagroberries.data.network.request.LoginRequest
import com.AgroberriesMX.transportesagroberries.data.network.response.LoginResponse
import com.AgroberriesMX.transportesagroberries.domain.Repository
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(
        loginRequest: LoginRequest
    ) = repository.getToken(loginRequest)
}