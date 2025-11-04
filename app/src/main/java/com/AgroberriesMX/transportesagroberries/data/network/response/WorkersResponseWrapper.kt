package com.AgroberriesMX.transportesagroberries.data.network.response

import com.google.gson.annotations.SerializedName

data class WorkersResponseWrapper(
@SerializedName("mensaje") val mensaje: String,
@SerializedName("response") val response: List<WorkerResponse>
)