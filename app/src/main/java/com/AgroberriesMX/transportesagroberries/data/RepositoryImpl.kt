package com.AgroberriesMX.transportesagroberries.data

import android.util.Log
import com.AgroberriesMX.transportesagroberries.data.local.DatabaseHelper
import com.AgroberriesMX.transportesagroberries.data.network.TransportApiService
import com.AgroberriesMX.transportesagroberries.data.network.request.LoginRequest
import com.AgroberriesMX.transportesagroberries.domain.Repository
import com.AgroberriesMX.transportesagroberries.domain.model.FormattedRecordsModel
import com.AgroberriesMX.transportesagroberries.domain.model.LoginModel
import com.AgroberriesMX.transportesagroberries.domain.model.RouteModel
import com.AgroberriesMX.transportesagroberries.domain.model.TokenModel
import com.AgroberriesMX.transportesagroberries.domain.model.VehicleModel
import com.AgroberriesMX.transportesagroberries.domain.model.WorkerModel
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val apiService: TransportApiService,
    private val dbHelper: DatabaseHelper // Agrega esto
) : Repository {
    companion object{
        private const val APP_INFO_TAG_KEY = "TransportesApp"
    }
    override suspend fun getToken(loginRequest: LoginRequest): TokenModel? {
        runCatching { apiService.login(loginRequest) }
            .onSuccess { return it.toDomain() }
            .onFailure { Log.i(APP_INFO_TAG_KEY, "Ha ocurrido un error ${it.message}") }

        return null
    }

    override suspend fun getLogins(token: String): LoginModel? {
        runCatching { apiService.loginsData(token) }
            .onSuccess { return it.toDomain() }
            .onFailure { Log.i(APP_INFO_TAG_KEY, "Ha ocurrido un error ${it.message}") }

        return null
    }

    /*override suspend fun getRoutes(): List<RouteModel>? {
        /*runCatching { apiService.routes(token) }
            .onSuccess { return it.toDomain() }
            .onFailure { Log.i(APP_INFO_TAG_KEY, "Ha ocurrido un error ${it.message}") }

        return null*/
        return try {
            val wrapper = apiService.routes()
            wrapper.response.map { it.toDomain() }
        } catch (e: Exception) {
            Log.e("RouteRepository", "Error al obtener rutas: ${e.message}")
            emptyList()
        }
    }*/
    override suspend fun getRoutes(): List<RouteModel>? {
        if (isNetworkAvailable()) {
            return try {
                val wrapper = apiService.routes()
                val routes = wrapper.response.map { it.toDomain() }

                // 1. Limpia los datos antiguos
                dbHelper.clearRoutes()
                // 2. Guarda los nuevos datos de la API en la base de datos local
                dbHelper.insertRoutes(routes)

                routes
            } catch (e: Exception) {
                Log.e("RouteRepository", "Error al obtener rutas de la API: ${e.message}")
                // 3. Como fallback, intenta obtener los datos de la DB local
                dbHelper.getAllRoutes()
            }
        } else {
            // 4. Si no hay internet, lee directamente de la DB local
            Log.i("RouteRepository", "Sin conexión, obteniendo rutas de la DB local.")
            return dbHelper.getAllRoutes()
        }
    }

    /*override suspend fun getVehicles(): List<VehicleModel>? {
        /*runCatching { apiService.vehicles() }
            .onSuccess { return it.map { dto -> dto.toDomain() } }
            .onFailure { Log.i(APP_INFO_TAG_KEY, "Ha ocurrido un error ${it.message}") }

        return null*/
        return try {
            val wrapper = apiService.vehicles()
            wrapper.response.map { it.toDomain() }
        } catch (e: Exception) {
            Log.e("VehicleRepository", "Error al obtener vehículos: ${e.message}")
            emptyList()
        }
    }*/

    override suspend fun getVehicles(): List<VehicleModel>? {
        // 1. Verifica la conexión a internet
        if (isNetworkAvailable()) {
            // Si hay internet, llama a la API
            return try {
                val wrapper = apiService.vehicles()
                val vehicles = wrapper.response.map { it.toDomain() }

                // 2. Borra los datos antiguos y guarda los nuevos en la DB local
                dbHelper.clearVehicles() // Debes crear esta función en el DatabaseHelper
                dbHelper.insertVehicles(vehicles)

                /*vehicles.forEach { vehicle ->
                    dbHelper.insertVehicles(vehicles)
                }*/

                vehicles
            } catch (e: Exception) {
                Log.e("VehicleRepository", "Error al obtener vehículos de la API: ${e.message}")
                // Si la API falla, intenta obtener los datos de la DB local como fallback
                dbHelper.getAllVehicles() // Debes crear esta función
            }
        } else {
            // Si no hay internet, lee directamente de la DB local
            Log.i("VehicleRepository", "Sin conexión, obteniendo datos de la DB local.")
            return dbHelper.getAllVehicles() // Debes crear esta función
        }
    }


   /* override suspend fun getWorkers(token: String): WorkerModel? {
        runCatching { apiService.workers(token) }
            .onSuccess { return it.toDomain() }
            .onFailure { Log.i(APP_INFO_TAG_KEY, "Ha ocurrido un error ${it.message}") }

        return null
    }*/

    override suspend fun getWorkers():  List<WorkerModel>? {
        // 1. Verifica la conexión a internet
        if (isNetworkAvailable()) {
            return try {
                // Llama a la API sin el token.
                val wrapper = apiService.workers()

                // Accede al campo 'response' dentro del wrapper para obtener la lista
                val workers = wrapper.response.map { it.toDomain() }

                // 2. Limpia los datos antiguos y guarda los nuevos
                dbHelper.clearWorkers()
                dbHelper.insertWorkers(workers)

                // Devuelve LA LISTA COMPLETA de trabajadores
                return workers
            } catch (e: Exception) {
                Log.e("WorkerRepository", "Error al obtener trabajadores de la API: ${e.message}")
                // 3. Como fallback, intenta obtener los datos de la DB local
                val localWorkers = dbHelper.getAllWorkers()
                return localWorkers
            }
        } else {
            // 4. Si no hay internet, lee directamente de la DB local
            Log.i("WorkerRepository", "Sin conexión, obteniendo trabajadores de la DB local.")
            val localWorkers = dbHelper.getAllWorkers()
            return localWorkers
        }
    }

    // Función auxiliar para verificar la conexión (debes implementarla)
    private fun isNetworkAvailable(): Boolean {
        // Aquí va tu lógica de verificación de red
        // Por ahora, puedes devolver 'true' para probar la lógica de la API
        // o 'false' para probar la DB local.
        return true // Sustituye esto con la lógica real
    }

    // ✅ ¡Implementa la función que faltaba aquí!
    override suspend fun uploadRecords(records: List<FormattedRecordsModel>): Pair<String?, Int?> {
        return try {
            val response = apiService.uploadData(records)

            if (response.isSuccessful) {
                Pair(response.body()?.message, response.code()) // O el formato de respuesta que manejes
            } else {
                Pair(response.message(), response.code())
            }
        } catch (e: Exception) {
            // Maneja los errores de red o excepciones
            Pair(e.message, null)
        }
    }
}