package com.AgroberriesMX.transportesagroberries.data.local

import com.AgroberriesMX.transportesagroberries.domain.model.RecordModel

interface TransportesLocalDBService {
    suspend fun getUnsynchronizedRecords(): List<RecordModel>?
    suspend fun listUnsynchronizedRecords(): List<RecordModel>?
    suspend fun updateRecord(
        cCodigoappTrn: Int,
        vChoferTrn: String,
        cCodigoTra: String,
        cFormaregTrn: String,
        dRegistroTrn: String,
        cTiporegTrn: String,
        cLongitudTrn: String,
        cLatitudTrn: String,
        cAlturaTrn: String,
        cCodigoUsu: String,
        dCreacionTrn: String,
        cControlVeh: Int,
        cControlRut: Int,
        nCostoRut: Double,
        cUsumodTrn: String,
        dModifiTrn: String,
        isSynced: Int
    ): Int?
}