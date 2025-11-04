package com.AgroberriesMX.transportesagroberries.data.network.request

import com.AgroberriesMX.transportesagroberries.domain.model.FormattedRecordsModel

class SyncRequest(
    val token: String,
    val data: List<FormattedRecordsModel>
)