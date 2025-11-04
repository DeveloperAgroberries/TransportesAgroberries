package com.AgroberriesMX.transportesagroberries.domain.usecase

import android.util.Log
import com.AgroberriesMX.transportesagroberries.domain.Repository
import com.AgroberriesMX.transportesagroberries.domain.model.FormattedRecordsModel
import javax.inject.Inject

class UploadUseCase @Inject constructor(private val repository: Repository) {
   suspend operator fun invoke (records: List<FormattedRecordsModel>):String {
        val response = repository.uploadRecords(records)
       Log.d("RecordsImp", "Datos que se van a enviar: $records") // <-- ¡Añade esta línea!
        return when (response.second) {
            200 -> "Ok"
            401 -> "Unauthorized"
            else -> response.first ?: "Error desconocido"
        }
    }
}