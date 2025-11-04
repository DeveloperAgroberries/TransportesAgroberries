package com.AgroberriesMX.transportesagroberries.data.network.request

import com.google.gson.annotations.SerializedName

data class UploadResponse(
    @SerializedName("message") val message: String
)