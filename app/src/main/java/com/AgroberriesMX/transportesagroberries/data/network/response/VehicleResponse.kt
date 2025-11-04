package com.AgroberriesMX.transportesagroberries.data.network.response

import com.AgroberriesMX.transportesagroberries.domain.model.VehicleModel
import com.google.gson.annotations.SerializedName

data class VehicleResponse(
    @SerializedName("cPlacaVeh") val cPlacaVeh: String,
    @SerializedName("cControlVeh") val cControlVeh: String
) {
    fun toDomain():VehicleModel{
        return VehicleModel(
            cPlacaVeh = cPlacaVeh,
            cControlVeh = cControlVeh
        )
    }
}