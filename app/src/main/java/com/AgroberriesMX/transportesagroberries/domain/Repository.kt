package com.AgroberriesMX.transportesagroberries.domain

import android.media.session.MediaSession.Token
import com.AgroberriesMX.transportesagroberries.data.network.request.LoginRequest
import com.AgroberriesMX.transportesagroberries.data.network.response.LoginResponse
import com.AgroberriesMX.transportesagroberries.domain.model.TokenModel
import retrofit2.Response

interface Repository {
    suspend fun getToken(loginRequest: LoginRequest): TokenModel?
}