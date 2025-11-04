package com.AgroberriesMX.transportesagroberries.domain

import com.AgroberriesMX.transportesagroberries.domain.model.RecordModel

interface RecordsRepository {
    suspend fun listUnsynchronizedRecords(): List<RecordModel>?
    suspend fun getUnsynchronizedRecords(): List<RecordModel>?
    suspend fun updateRecord(record: RecordModel): Int?
}