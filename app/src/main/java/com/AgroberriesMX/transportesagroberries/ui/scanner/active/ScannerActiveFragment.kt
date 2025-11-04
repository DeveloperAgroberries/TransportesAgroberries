package com.AgroberriesMX.transportesagroberries.ui.scanner.active

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.AgroberriesMX.transportesagroberries.R
import com.AgroberriesMX.transportesagroberries.data.local.DatabaseHelper
import com.AgroberriesMX.transportesagroberries.databinding.FragmentScannerActiveBinding
import com.AgroberriesMX.transportesagroberries.domain.model.WorkerModel
import com.AgroberriesMX.transportesagroberries.ui.shared.SharedViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

// Constantes para los códigos de solicitud de permisos
private const val CAMERA_PERMISSION_REQUEST_CODE = 100
private const val LOCATION_PERMISSION_REQUEST_CODE = 101

// ⭐ Nuevo: Tiempo en milisegundos para permitir volver a escanear (2 horas)
private const val RE_SCAN_INTERVAL_MILLIS = 2 * 60 * 60 * 1000L // 2 horas
//private const val RE_SCAN_INTERVAL_MILLIS = 1 * 60 * 1000L // 1 minuto

// ⭐ Nuevo: Tiempo mínimo entre escaneos del mismo código para evitar "falsos positivos"
private const val MIN_SCAN_DELAY_MILLIS = 2000L // 2 segundos

@AndroidEntryPoint
class ScannerActiveFragment : Fragment() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var isRequestingLocationUpdates = false

    private var pendingInsertNewCode: String? = null
    private var pendingInsertWorker: WorkerModel? = null

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var sessionPrefs: SharedPreferences

    @Inject
    lateinit var dbHelper: DatabaseHelper

    private var _binding: FragmentScannerActiveBinding? = null
    private val binding get() = _binding!!

    private lateinit var barcodeView: DecoratedBarcodeView
    private var isFlashOn = false
    // ⭐ CAMBIO: Ahora un mapa para almacenar el código y su último timestamp de registro
    private val registeredWorkersLastScanTime = mutableMapOf<String, Long>()
    private var lastScannedCode: String? = null
    private var lastScanTime: Long = 0L
    private var mediaPlayer: MediaPlayer? = null
    private val recordDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    //private val recordDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)

    //init {
     //   recordDateFormat.timeZone = TimeZone.getTimeZone("UTC")
    //}


    // BarcodeCallback: Lógica de escaneo
    private val callback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult?) {
            result?.text?.let { newCode ->
                val currentTime = System.currentTimeMillis()

                // 1. Evitar escaneos duplicados muy rápidos (Antiflicking)
                if (currentTime - lastScanTime < MIN_SCAN_DELAY_MILLIS) {
                    return
                }

                val worker = dbHelper.workerExists(newCode)
                Log.d("revisarCodigo", "Codigo: ${newCode}")
                if (worker != null) {

                    // ⭐ 2. Validar tiempo en la base de datos
                    val lastRecordTimeStr = dbHelper.getLatestTransportRecordTime(newCode)

                    Log.d("revisarTiempo", "Tiempo: ${lastRecordTimeStr}")

                    if (lastRecordTimeStr == null) {
                        // NO existe registro, puede escanear.
                        playScanBeep()
                        requestLocationForRecord(newCode, worker)
                    } else {
                        // Existe registro, verifica el tiempo.

                        // ⭐ CAMBIO CRÍTICO: Usar try-catch para manejar el posible error de parseo
                        val lastScanDate = try {
                            recordDateFormat.parse(lastRecordTimeStr)
                        } catch (e: Exception) {
                            Log.e("ScannerActiveFragment", "Error de parseo de fecha: ${e.message}")
                            null
                        }
                        Log.d("validarConteoTiempo", "Tiempo: ${lastScanDate}")

                        // ⭐ Aquí está el bug potencial: si el parseo falla, lastScanTimeMillis es 0L.
                        val lastScanTimeMillis = lastScanDate?.time ?: 0L
                        Log.d("¿Que es?", "Tiempo: ${lastScanTimeMillis}")

                        if (lastScanTimeMillis == 0L) {
                            // Si es 0L, significa que falló el parseo, lo que DEBE considerarse una denegación o error
                            // para no burlar la restricción de 2 horas.
                            Log.e("ScannerActiveFragment", "Error grave: El parseo de fecha falló (0L). Denegando escaneo por seguridad.")
                            playErrorBeep()
                            Toast.makeText(requireContext(), "Error de validación de tiempo. Contacte a soporte.", Toast.LENGTH_LONG).show()
                            return // Salir y no insertar
                        }

                        val timeDifference = currentTime - lastScanTimeMillis

                        Log.d("Diferencia tiempo", "Tiempo: ${timeDifference}, Limite: ${RE_SCAN_INTERVAL_MILLIS}")

                        if (timeDifference > RE_SCAN_INTERVAL_MILLIS) {
                            // Han pasado más de 2 horas, puede re-escanear.
                            playScanBeep()
                            requestLocationForRecord(newCode, worker)
                        } else {
                            // NO han pasado 2 horas, denegar y mostrar tiempo restante.
                            playErrorBeep()
                            val timeRemaining = RE_SCAN_INTERVAL_MILLIS - timeDifference
                            val hours = timeRemaining / (60 * 60 * 1000)
                            val minutes = (timeRemaining % (60 * 60 * 1000)) / (60 * 1000)

                            Toast.makeText(
                                requireContext(),
                                //"Trabajador ${worker.vNombreTra} ya fue registrado. Puede volver a escanear en ${hours}h ${minutes}m.",
                                "Trabajador ${worker.vNombreUsu} ya fue registrado.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } else {
                    playErrorBeep()
                    Toast.makeText(requireContext(), "Código no válido: $newCode", Toast.LENGTH_LONG).show()
                }

                lastScannedCode = newCode
                lastScanTime = currentTime // Actualiza el tiempo del último escaneo general
            }
        }
        // ... (El resto del fragmento se mantiene)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScannerActiveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        sessionPrefs = requireContext().getSharedPreferences("session_prefs", Context.MODE_PRIVATE)

        barcodeView = binding.barcodeScannerView

        // ✅ LLAMAR A LAS NUEVAS FUNCIONES DE CHEQUEO:
        checkAndRequestCameraPermission()
        //checkAndRequestLocationPermissions() // Esto es lo que va a ejecutar la lógica de chequeo/lanzamiento

        binding.btnFlash.setOnClickListener {
            toggleFlash()
        }

        updateRegisteredWorkersCount()

        // ⭐ Inicialización de LocationCallback
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    Log.d("ScannerActiveFragment", "Ubicación recibida: ${location.latitude}, ${location.longitude}")

                    pendingInsertNewCode?.let { newCode ->
                        pendingInsertWorker?.let { worker ->
                            insertTransportRecordWithLocation(newCode, worker, location)
                            pendingInsertNewCode = null
                            pendingInsertWorker = null
                            stopLocationUpdates() // Detener actualizaciones una vez que se obtiene la ubicación
                            return
                        }
                    }
                    stopLocationUpdates() // Detener si llegó una ubicación pero no había nada pendiente
                }
            }

            override fun onLocationAvailability(p0: com.google.android.gms.location.LocationAvailability) {
                super.onLocationAvailability(p0)
                if (!p0.isLocationAvailable && isRequestingLocationUpdates) {
                    Log.w("ScannerActiveFragment", "Servicios de ubicación no disponibles o señal débil.")
                    // Podrías mostrar un Toast aquí si lo consideras necesario.
                }
            }
        }
    }

    // Función de chequeo y solicitud para la CÁMARA (la que inicias en onViewCreated)
    private fun checkAndRequestCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // Si la cámara ya está concedida, vamos DIRECTO a checar/pedir el siguiente permiso.
            checkAndRequestLocationPermissions()
        } else {
            // Si la cámara no está concedida, lanzamos el primer diálogo (el launcher de cámara
            // se encargará de pedir la ubicación después).
            checkCameraPermission.launch(Manifest.permission.CAMERA)
        }
    }

    // Función de chequeo y solicitud para la UBICACIÓN
    private fun checkAndRequestLocationPermissions() {
        // Si la ubicación precisa NO está concedida, lanzamos la solicitud.
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationPermission.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            // Si ambos permisos (Cámara y Ubicación) ya estaban concedidos,
            // simplemente iniciamos el escáner, que es la acción final.
            startScanner()
        }
    }

    // Launcher de Cámara
    private val checkCameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // PERMISO DE CÁMARA CONCEDIDO:
            // ¡NO iniciar el escáner todavía! Continuar con la solicitud de ubicación.
            checkAndRequestLocationPermissions()

            // NOTA: Si necesitas que la lógica de ubicación se ejecute incluso si ya tiene el permiso,
            // puedes llamar directamente a su launcher aquí:
            // locationPermission.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))

        } else {
            // Cámara denegada, no podemos escanear.
            Toast.makeText(requireContext(), "Se necesita el permiso de cámara para la funcionalidad de escaneo.", Toast.LENGTH_LONG).show()
        }
    }

    // Launcher de Ubicación (maneja múltiples permisos)
    private val locationPermission = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions: Map<String, Boolean> ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        // ********* PUNTO CRÍTICO *********
        // Después de que el usuario interactúa con los permisos, SIEMPRE iniciamos el scanner.
        // Esto asegura que la cámara se abra independientemente del GPS (si el permiso de cámara ya está OK).
        startScanner()
        // **********************************

        if (fineLocationGranted || coarseLocationGranted) {
            // Permiso concedido. Puedes iniciar actualizaciones aquí si las necesitas inmediatamente.
            // startLocationUpdates() // NO ES NECESARIO AQUÍ, ya que se inicia en requestLocationForRecord()
            Toast.makeText(requireContext(), "Permiso de ubicación concedido.", Toast.LENGTH_SHORT).show()
        } else {
            // Permiso denegado.
            Toast.makeText(requireContext(), "Permiso de ubicación denegado. Se registrará sin GPS.", Toast.LENGTH_LONG).show()
        }
    }

    private fun startScanner() {
        barcodeView.decodeContinuous(callback)
    }

    /*override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startScanner()
                } else {
                    Toast.makeText(requireContext(), "Se necesita el permiso de cámara para la funcionalidad de escaneo.", Toast.LENGTH_LONG).show()
                }
            }
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(requireContext(), "Permiso de ubicación concedido.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Permiso de ubicación denegado. Se registrará sin GPS.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }*/

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
            .setMinUpdateIntervalMillis(2500)
            .setMaxUpdateDelayMillis(10000)
            .build()

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
            isRequestingLocationUpdates = true
            Log.d("ScannerActiveFragment", "Iniciando actualizaciones de ubicación...")
        } else {
            Log.w("ScannerActiveFragment", "Permisos de ubicación no concedidos al intentar iniciar actualizaciones. Esto no debería pasar.")
            // En caso de que se intente iniciar actualizaciones sin permiso (aunque checkLocationPermissions() debería evitarlo)
            // Procedemos a insertar sin ubicación.
            pendingInsertNewCode?.let { newCode ->
                pendingInsertWorker?.let { worker ->
                    insertTransportRecordWithLocation(newCode, worker, null)
                    pendingInsertNewCode = null
                    pendingInsertWorker = null
                }
            }
        }
    }

    private fun stopLocationUpdates() {
        if (isRequestingLocationUpdates) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
            isRequestingLocationUpdates = false
            Log.d("ScannerActiveFragment", "Deteniendo actualizaciones de ubicación.")
        }
    }

    private fun toggleFlash() {
        try {
            isFlashOn = !isFlashOn
            if (isFlashOn) {
                barcodeView.setTorchOn()
                Toast.makeText(requireContext(), "Flash: Encendido", Toast.LENGTH_SHORT).show()
            } else {
                barcodeView.setTorchOff()
                Toast.makeText(requireContext(), "Flash: Apagado", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error al controlar el flash", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateRegisteredWorkersCount() {
        //binding.tvRegisteredCount.text = registeredWorkersLastScanTime.size.toString()
        // Llama a la nueva función del DBHelper para obtener el conteo de hoy
        val count = dbHelper.getTodayUniqueWorkersCount()
        binding.tvRegisteredCount.text = count.toString()
    }

    private fun playScanBeep() {
        context?.let { safeContext ->
            mediaPlayer?.release()
            mediaPlayer = null
            try {
                mediaPlayer = MediaPlayer.create(safeContext, R.raw.beep)
                mediaPlayer?.start()
                mediaPlayer?.setOnCompletionListener { mp ->
                    mp.release()
                    mediaPlayer = null
                }
                Log.d("ScannerActiveFragment", "Beep de escaneo reproducido.")
            } catch (e: Exception) {
                Log.e("ScannerActiveFragment", "Error al reproducir el beep de escaneo: ${e.message}")
                Toast.makeText(safeContext, "Error de sonido (BEEP)", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Log.e("ScannerActiveFragment", "Contexto nulo al intentar reproducir beep de escaneo.")
        }
    }

    private fun playErrorBeep() {
        context?.let { safeContext ->
            mediaPlayer?.release()
            mediaPlayer = null
            try {
                mediaPlayer = MediaPlayer.create(safeContext, R.raw.error)
                mediaPlayer?.start()
                mediaPlayer?.setOnCompletionListener { mp ->
                    mp.release()
                    mediaPlayer = null
                }
                Log.d("ScannerActiveFragment", "Beep de error reproducido.")
            } catch (e: Exception) {
                Log.e("ScannerActiveFragment", "Error al reproducir el beep de error: ${e.message}")
                Toast.makeText(safeContext, "Error de sonido (ERROR)", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Log.e("ScannerActiveFragment", "Contexto nulo al intentar reproducir beep de error.")
        }
    }

    private fun requestLocationForRecord(newCode: String, worker: WorkerModel) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            pendingInsertNewCode = newCode
            pendingInsertWorker = worker

            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null && (System.currentTimeMillis() - location.time < 30000)) {
                    Log.d("ScannerActiveFragment", "Usando lastLocation (fresca). Lat: ${location.latitude}, Lon: ${location.longitude}")
                    insertTransportRecordWithLocation(newCode, worker, location)
                    pendingInsertNewCode = null
                    pendingInsertWorker = null
                } else {
                    Log.d("ScannerActiveFragment", "lastLocation no es fresca o es nula, solicitando actualizaciones.")
                    startLocationUpdates()
                }
            }.addOnFailureListener { e ->
                Log.e("ScannerActiveFragment", "Error al obtener lastLocation: ${e.message}, solicitando actualizaciones.")
                startLocationUpdates()
            }
        } else {
            Log.w("ScannerActiveFragment", "Permiso de ubicación no concedido para registrar. Insertando sin ubicación.")
            insertTransportRecordWithLocation(newCode, worker, null)
        }
    }

    private fun insertTransportRecordWithLocation(newCode: String, worker: WorkerModel, location: Location?) {
        val selectedVehicleCode = sharedViewModel.selectedVehicleCode.value ?: 0
        val selectedRouteCode = sharedViewModel.selectedRouteCode.value ?: 0
        val selectedRouteCost = sharedViewModel.selectedRouteCost.value ?: 0.0
        val driverName = sharedViewModel.driverName.value ?: "Sin Nombre"
        val cCodigoUsu = sessionPrefs.getString("cCodigoUsu", "") ?: ""

        // 1. Obtener la hora actual
        val mexicoTimeZone = TimeZone.getTimeZone("America/Mexico_City")
        val calendar = Calendar.getInstance(mexicoTimeZone)
        // 2. Obtener la hora del día en formato de 24 horas (HOUR_OF_DAY)
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)

        val subidaBajada = dbHelper.getSubidaBajada(newCode)

        // 3. Realizar la validación
        // Si la hora es MAYOR o IGUAL a 12 (12:00 PM o después), cTiporegTrn = "1"
        // Si la hora es MENOR a 12 (antes de 12:00 PM), cTiporegTrn = "0"
        val cTiporegTrnValue = if (subidaBajada == "0") {
            "0"
        } else {
            "1"
        }

        // 1. Obtener la hora actual usando la zona horaria de México

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

        val success = dbHelper.insertTransportRecord(
            cCodigoTra = newCode,
            vChoferTrn = driverName,
            cFormaregTrn = "1",
            dRegistroTrn = dRegistroTrnValue + "Z",
            cTiporegTrn = cTiporegTrnValue,
            cCodigoUsu = cCodigoUsu,
            dCreacionTrn = dRegistroTrnValue + "Z",
            cControlVeh = selectedVehicleCode,
            cControlRut = selectedRouteCode,
            nCostoRut = selectedRouteCost,
            location = location
        )

        if (success) {
            //playScanBeep()
            updateRegisteredWorkersCount() // ¡AQUÍ!
            Toast.makeText(requireContext(), "Trabajador ${worker.vNombreUsu} registrado.", Toast.LENGTH_SHORT).show()
        } else {
            playErrorBeep()
            Toast.makeText(requireContext(), "Error al registrar", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        if (::barcodeView.isInitialized) {
            barcodeView.resume()
        }
        // stopLocationUpdates() // No iniciar aquí, solo cuando se necesite al escanear
    }

    override fun onPause() {
        super.onPause()
        if (::barcodeView.isInitialized) {
            barcodeView.pause()
        }
        stopLocationUpdates()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mediaPlayer?.release()
        mediaPlayer = null
    }
}