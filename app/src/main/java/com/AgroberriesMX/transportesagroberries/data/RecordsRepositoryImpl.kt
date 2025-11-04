package com.AgroberriesMX.transportesagroberries.data

import com.AgroberriesMX.transportesagroberries.data.local.TransportesLocalDBService
import com.AgroberriesMX.transportesagroberries.domain.RecordsRepository
import com.AgroberriesMX.transportesagroberries.domain.model.RecordModel
import javax.inject.Inject

class RecordsRepositoryImpl @Inject constructor(
    private val localDBService: TransportesLocalDBService
) : RecordsRepository {
    override suspend fun listUnsynchronizedRecords(): List<RecordModel>? {
        return localDBService.listUnsynchronizedRecords()
    }

    override suspend fun getUnsynchronizedRecords(): List<RecordModel>? {
        return localDBService.getUnsynchronizedRecords()
    }

    override suspend fun updateRecord(record: RecordModel): Int? {
        return localDBService.updateRecord(
            record.cCodigoappTrn,
            record.vChoferTrn,
            record.cCodigoTra,
            record.cFormaregTrn,
            record.dRegistroTrn,
            record.cTiporegTrn,
            record.cLongitudTrn,
            record.cLatitudTrn,
            record.cAlturaTrn,
            record.cCodigoUsu,
            record.dCreacionTrn,
            record.cControlVeh,
            record.cControlRut,
            record.nCostoRut,
            record.cUsumodTrn,
            record.dModifiTrn,
            record.isSynced
        )
    }
}