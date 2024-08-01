package com.AgroberriesMX.transportesagroberries.data.network.response

import com.AgroberriesMX.transportesagroberries.domain.model.TokenModel
import com.google.gson.annotations.SerializedName

data class LoginResponse (
    @SerializedName("token") val token: String
) {
    fun toDomain(): TokenModel {
        return TokenModel(
            token = token
        )
    }
}