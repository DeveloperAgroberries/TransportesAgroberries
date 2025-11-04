package com.AgroberriesMX.transportesagroberries.data.network.response

import com.AgroberriesMX.transportesagroberries.domain.model.WorkerModel
import com.google.gson.annotations.SerializedName

data class WorkerResponse(
    @SerializedName("cCodigoUsu") val cCodigoUsu: String,
    @SerializedName("vNombreUsu") val vNombreUsu: String,
    /*@SerializedName("cCodigoTra") val cCodigoTra: String,
    @SerializedName("vNombreTra") val vNombreTra: String,
    @SerializedName("vApellidopatTra") val vApellidopatTra: String,
    @SerializedName("vApellidomatTra") val vApellidomatTra: String,
    @SerializedName("cCodigoLug") val cCodigoLug: String*/
) {
    fun toDomain(): WorkerModel {
        return WorkerModel(
            cCodigoUsu = cCodigoUsu,
            vNombreUsu = vNombreUsu,
            /*cCodigoTra = cCodigoTra,
            vNombreTra = vNombreTra,
            vApellidopatTra = vApellidopatTra,
            vApellidomatTra = vApellidomatTra,
            cCodigoLug = cCodigoLug*/
        )
    }
}