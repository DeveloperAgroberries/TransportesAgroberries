package com.AgroberriesMX.transportesagroberries.ui.sync

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.AgroberriesMX.transportesagroberries.data.local.TransportesLocalDBService
import com.AgroberriesMX.transportesagroberries.domain.RecordsRepository
import com.AgroberriesMX.transportesagroberries.domain.model.FormattedRecordsModel
import com.AgroberriesMX.transportesagroberries.domain.model.RecordModel
import com.AgroberriesMX.transportesagroberries.domain.usecase.RouteUseCase
import com.AgroberriesMX.transportesagroberries.domain.usecase.UploadUseCase
import com.AgroberriesMX.transportesagroberries.domain.usecase.VehicleUseCase
import com.AgroberriesMX.transportesagroberries.domain.usecase.WorkerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

@HiltViewModel
class SyncViewModel @Inject constructor(
    private val workerUseCase: WorkerUseCase,
    private val routeUseCase: RouteUseCase,
    private val vehicleUseCase: VehicleUseCase,
    private val databaseService: TransportesLocalDBService,
    private val recordsRepository: RecordsRepository,
    private val uploadUseCase: UploadUseCase
) : ViewModel() {

    private val _state = MutableLiveData<SyncState>(SyncState.Waiting)
    val state: LiveData<SyncState> get() = _state

    private val _pendingRecords = MutableLiveData<List<RecordModel>>()
    val pendingRecords: LiveData<List<RecordModel>> get() = _pendingRecords

    fun sync(token: String) {
        viewModelScope.launch {
            _state.value = SyncState.Loading
            try {
                workerUseCase.invoke()
                routeUseCase.invoke()
                vehicleUseCase.invoke()
                _state.value = SyncState.CatalogSuccess
            } catch (e: Exception) {
                _state.value = SyncState.Error("Ha ocurrido un error al sincronizar catálogos")
            }
        }
    }

    fun loadPendingRecords() {
        viewModelScope.launch {
            val records = recordsRepository.listUnsynchronizedRecords()
            _pendingRecords.value = records ?: emptyList()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun upload() {
        viewModelScope.launch {
            _state.value = SyncState.Loading
            try {
                val localData = recordsRepository.getUnsynchronizedRecords()

                if (localData != null && localData.isNotEmpty()) {
                    val transformedData: List<FormattedRecordsModel> = localData.map { register ->
                        //val serverFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
                        //val now = ZonedDateTime.now(ZoneId.of("UTC"))
                        val cUsumodTrnValue = if (register.cUsumodTrn.isNullOrEmpty()) null else register.cUsumodTrn
                        val dModifiTrnValue = if (register.dModifiTrn.isNullOrEmpty()) null else register.dModifiTrn

                        // 1. Obtener la hora actual usando la zona horaria de México
                        val mexicoTimeZone = TimeZone.getTimeZone("America/Mexico_City")
                        val calendar = Calendar.getInstance(mexicoTimeZone)
                        val localTimeInMexico = calendar.time

                        // 2. Definir el formateador de salida con el patrón exacto, pero SIN la 'Z'
                        val isoFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())

                        // 3. CRÍTICO: Forzar al formateador a usar la hora de MÉXICO para que imprima 14:29.
                        // NO USAR UTC.
                        isoFormatter.timeZone = mexicoTimeZone

                        // 4. Formatear la hora
                        val dRegistroTrnValue = isoFormatter.format(localTimeInMexico)

                        // Resultado del log: 2025-10-09T14:29:39.455 (Hora Local de México)
                        //Log.d("SalidaServidor", "Valor enviado (LOCAL): $dRegistroTrnValue")

                        FormattedRecordsModel(
                            cCodigoappTrn = register.cCodigoappTrn,
                            vChoferTrn = register.vChoferTrn,
                            cCodigoTra = register.cCodigoTra.trim(),
                            cFormaregTrn = register.cFormaregTrn.toString(),
                            dRegistroTrn = register.dRegistroTrn,
                            cTiporegTrn = register.cTiporegTrn.toString(),
                            cLongitudTrn = register.cLongitudTrn.toString(),
                            cLatitudTrn = register.cLatitudTrn.toString(),
                            cAlturaTrn = register.cAlturaTrn.toString(),
                            cCodigoUsu = register.cCodigoUsu,
                            dCreacionTrn = dRegistroTrnValue + "Z",
                            cControlVeh = register.cControlVeh,
                            cControlRut = register.cControlRut,
                            nCostoRut = register.nCostoRut,
                            cUsumodTrn = cUsumodTrnValue,
                            dModifiTrn = dModifiTrnValue
                        )
                    }

                    val response = uploadUseCase(transformedData)
                    //Log.d("ResponseServer", "Respuesta: $response") // <-- ¡Añade esta línea!

                    if (response == "Ok") {
                        localData.forEach { record ->
                            record.isSynced = 1
                            recordsRepository.updateRecord(record)
                        }
                        _state.value = SyncState.UploadSuccess("Datos enviados correctamente")
                        loadPendingRecords()
                    } else {
                        if (response == "Unauthorized") {
                            _state.value = SyncState.Error("No cuentas con un token para enviar los datos, cierra e inicia sesion y vuelve a intentarlo, por favor.")
                        } else {
                            _state.value = SyncState.Error(response)
                        }
                    }
                } else {
                    _state.value = SyncState.Error("No hay nada que enviar")
                }
            } catch (e: Exception) {
                _state.value = SyncState.Error(e.message ?: "Ha ocurrido un error")
            }
        }
    }

    fun clearState() {
        _state.value = SyncState.Waiting
    }
}