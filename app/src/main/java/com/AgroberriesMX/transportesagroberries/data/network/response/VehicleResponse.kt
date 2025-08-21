package com.AgroberriesMX.transportesagroberries.data.network.response

import com.AgroberriesMX.transportesagroberries.domain.model.VehicleModel
import com.google.gson.annotations.SerializedName

data class VehicleResponse(
    @SerializedName("cPlacaVeh") val cPlacaVeh: String,
    @SerializedName("cControlPrv") val cControlPrv: String
) {
    fun toDomain():VehicleModel{
        return VehicleModel(
            cPlacaVeh = cPlacaVeh,
            cControlPrv = cControlPrv
        )
    }
}