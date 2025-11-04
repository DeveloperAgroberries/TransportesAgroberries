package com.AgroberriesMX.transportesagroberries.data.local

import com.AgroberriesMX.transportesagroberries.domain.model.RecordModel
import javax.inject.Inject

class TransportesLocalDBServiceImpl @Inject constructor(private val databaseHelper: DatabaseHelper) :
    TransportesLocalDBService {

    override suspend fun listUnsynchronizedRecords(): List<RecordModel>? {
        return databaseHelper.listUnsynchronizedRecords()
    }

    override suspend fun getUnsynchronizedRecords(): List<RecordModel>? {
        return databaseHelper.getUnsynchronizedRecords()
    }

    override suspend fun updateRecord(
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
    ): Int? {
        return databaseHelper.updateRecord(
            cCodigoappTrn,
            vChoferTrn,
            cCodigoTra,
            cFormaregTrn,
            dRegistroTrn,
            cTiporegTrn,
            cLongitudTrn,
            cLatitudTrn,
            cAlturaTrn,
            cCodigoUsu,
            dCreacionTrn,
            cControlVeh,
            cControlRut,
            nCostoRut,
            cUsumodTrn,
            dModifiTrn,
            isSynced
        )
    }

}