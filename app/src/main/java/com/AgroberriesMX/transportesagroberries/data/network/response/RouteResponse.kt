package com.AgroberriesMX.transportesagroberries.data.network.response

import com.AgroberriesMX.transportesagroberries.domain.model.RouteModel
import com.google.gson.annotations.SerializedName

data class RouteResponse(
    @SerializedName("cControlRut") val cControlRut:String,
    @SerializedName("vDescripcionRut") val vDescripcionRut:String
)
{
    fun toDomain():RouteModel{
        return RouteModel(
            cControlRut = cControlRut,
            vDescripcionRut = vDescripcionRut
        )
    }
}