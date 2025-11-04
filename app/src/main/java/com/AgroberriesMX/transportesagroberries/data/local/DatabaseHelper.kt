package com.AgroberriesMX.transportesagroberries.data.local

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.location.Location
import android.util.Log
import android.widget.Toast
import com.AgroberriesMX.transportesagroberries.domain.model.RecordModel
import com.AgroberriesMX.transportesagroberries.domain.model.RouteModel
import com.AgroberriesMX.transportesagroberries.domain.model.VehicleModel
import com.AgroberriesMX.transportesagroberries.domain.model.WorkerModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseHelper @Inject constructor(@ApplicationContext private val context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "agrotrasnport.db"
        private const val DATABASE_VERSION = 5

        private const val CREATE_TABLE_LOGINS = """
            CREATE TABLE genlogin (
                controlLog INTEGER PRIMARY KEY AUTOINCREMENT,
                cCodigoUsu TEXT,
                vNombreUsu TEXT
            )
        """

        private const val CREATE_TABLE_ROUTES = """
            CREATE TABLE z_nomruta (
                controlLog INTEGER PRIMARY KEY AUTOINCREMENT,
                cControlRut TEXT,
                vDescripcionRut TEXT,
                nCostoRut TEXT,  -- ¡NUEVA COLUMNA!
                cActivaRuta TEXT
            )
        """

        private const val CREATE_TABLE_VEHICLES = """
            CREATE TABLE z_nomvehiculo (
                controlLog INTEGER PRIMARY KEY AUTOINCREMENT,
                cPlacaVeh TEXT,
                cControlVeh INTEGER
            )
        """

        private const val CREATE_TABLE_WORKERS = """
            CREATE TABLE nomtrabajador (
                controlLog INTEGER PRIMARY KEY AUTOINCREMENT,
                cCodigoUsu TEXT,
                vNombreUsu TEXT
                /*cCodigoTra TEXT,
                cCodigoLug TEXT,
                vNombreTra TEXT,
                vApellidopatTra TEXT,
                vApellidomatTra TEXT*/
            )
        """

        private const val CREATE_TABLE_REGISTERS = """
            CREATE TABLE z_nomtransportes (
                controlLog INTEGER PRIMARY KEY AUTOINCREMENT,
                cCodigoappTrn TEXT,
                vChoferTrn TEXT,
                cCodigoTra TEXT,
                cFormaregTrn TEXT,
                dRegistroTrn TEXT,
                cTiporegTrn TEXT,
                cLongitudTrn TEXT,
                cLatitudTrn TEXT,
                cAlturaTrn TEXT,
                cCodigoUsu TEXT,
                dCreacionTrn TEXT,
                cControlVeh INTEGER,
                cControlRut INTEGER,
                nCostoRut REAL,
                cUsumodTrn TEXT,
                dModifiTrn TEXT,
                isSynced INTEGER DEFAULT 0
            )
        """
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_LOGINS)
        db.execSQL(CREATE_TABLE_ROUTES)
        db.execSQL(CREATE_TABLE_VEHICLES)
        db.execSQL(CREATE_TABLE_WORKERS)
        db.execSQL(CREATE_TABLE_REGISTERS)
    }

    private fun dropTables(db: SQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS genlogin")
        db.execSQL("DROP TABLE IF EXISTS z_nomruta")
        db.execSQL("DROP TABLE IF EXISTS z_nomvehiculo")
        db.execSQL("DROP TABLE IF EXISTS nomtrabajador")
        db.execSQL("DROP TABLE IF EXISTS z_nomtransportes")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        dropTables(db)
        onCreate(db)
    }

    // --- FUNCIONES DE VEHICULOS ---
    fun insertVehicles(vehicles: List<VehicleModel>) {
        val db = this.writableDatabase
        db.beginTransaction()
        try {
            vehicles.forEach { vehicle ->
                val values = ContentValues().apply {
                    put("cPlacaVeh", vehicle.cPlacaVeh)
                    put("cControlVeh", vehicle.cControlVeh)
                }
                db.insert("z_nomvehiculo", null, values)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun getAllVehicles(): List<VehicleModel> {
        val records = mutableListOf<VehicleModel>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM z_nomvehiculo", null)
        try {
            if (cursor.moveToFirst()) {
                val placaIndex = cursor.getColumnIndex("cPlacaVeh")
                val controlIndex = cursor.getColumnIndex("cControlVeh")
                do {
                    val record = VehicleModel(
                        cPlacaVeh = cursor.getString(placaIndex),
                        cControlVeh = cursor.getString(controlIndex)
                    )
                    records.add(record)
                } while (cursor.moveToNext())
            }
        } finally {
            cursor.close()
        }
        return records
    }

    fun clearVehicles() {
        this.writableDatabase.delete("z_nomvehiculo", null, null)
    }

    // --- FUNCIONES DE RUTAS ---
    fun insertRoutes(routes: List<RouteModel>) {
        val db = this.writableDatabase
        db.beginTransaction()
        try {
            routes.forEach { route ->
                val values = ContentValues().apply {
                    put("cControlRut", route.cControlRut)
                    put("vDescripcionRut", route.vDescripcionRut)
                    put("nCostoRut", route.nCostoRut)
                }
                db.insert("z_nomruta", null, values)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun getAllRoutes(): List<RouteModel> {
        val records = mutableListOf<RouteModel>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM z_nomruta", null)
        try {
            if (cursor.moveToFirst()) {
                val controlIndex = cursor.getColumnIndex("cControlRut")
                val descripcionIndex = cursor.getColumnIndex("vDescripcionRut")
                val costoIndex = cursor.getColumnIndex("nCostoRut")
                do {
                    val record = RouteModel(
                        cControlRut = cursor.getString(controlIndex),
                        vDescripcionRut = cursor.getString(descripcionIndex),
                        nCostoRut = cursor.getString(costoIndex)
                    )
                    records.add(record)
                } while (cursor.moveToNext())
            }
        } finally {
            cursor.close()
        }
        return records
    }

    fun clearRoutes() {
        this.writableDatabase.delete("z_nomruta", null, null)
    }

    // --- FUNCIONES DE TRABAJADORES ---
    fun insertWorkers(workers: List<WorkerModel>) {
        val db = this.writableDatabase
        db.beginTransaction()
        try {
            workers.forEach { worker ->
                val values = ContentValues().apply {
                    put("cCodigoUsu", worker.cCodigoUsu.trim())
                    put("vNombreUsu", worker.vNombreUsu.trim())
                    /*put("cCodigoTra", worker.cCodigoTra.trim())
                    put("vNombreTra", worker.vNombreTra.trim())
                    put("vApellidopatTra", worker.vApellidopatTra.trim())
                    put("vApellidomatTra", worker.vApellidomatTra.trim())
                    put("cCodigoLug", worker.cCodigoLug.trim())*/
                }
                db.insert("nomtrabajador", null, values)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun getAllWorkers(): List<WorkerModel> {
        val records = mutableListOf<WorkerModel>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM nomtrabajador", null)
        try {
            if (cursor.moveToFirst()) {
                val cCodigoUsuIndex = cursor.getColumnIndex("cCodigoUsu")
                val vNombreUsuIndex = cursor.getColumnIndex("vNombreUsu")
                /*val codigoTraIndex = cursor.getColumnIndex("cCodigoTra")
                val codigoLugIndex = cursor.getColumnIndex("cCodigoLug")
                val nombreTraIndex = cursor.getColumnIndex("vNombreTra")
                val apellidopatTraIndex = cursor.getColumnIndex("vApellidopatTra")
                val apellidomatTraIndex = cursor.getColumnIndex("vApellidomatTra")*/
                do {
                    val record = WorkerModel(
                        cCodigoUsu = cursor.getString(cCodigoUsuIndex),
                        vNombreUsu = cursor.getString(vNombreUsuIndex)
                        /*cCodigoTra = cursor.getString(codigoTraIndex),
                        cCodigoLug = cursor.getString(codigoLugIndex),
                        vNombreTra = cursor.getString(nombreTraIndex),
                        vApellidopatTra = cursor.getString(apellidopatTraIndex),
                        vApellidomatTra = cursor.getString(apellidomatTraIndex)*/
                    )
                    records.add(record)
                } while (cursor.moveToNext())
            }
        } finally {
            cursor.close()
        }
        return records
    }

    fun workerExists(cCodigoUsu: String): WorkerModel? {
        var worker: WorkerModel? = null
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.query(
                "nomtrabajador",
                // Aseguramos que todas las columnas necesarias estén en la consulta
                arrayOf("cCodigoUsu", "vNombreUsu"),
                "cCodigoUsu = ?",
                arrayOf(cCodigoUsu.trim()),
                null,
                null,
                null
                /*"nomtrabajador",
                // Aseguramos que todas las columnas necesarias estén en la consulta
                arrayOf("cCodigoTra", "vNombreTra", "vApellidopatTra", "vApellidomatTra", "cCodigoLug"),
                "cCodigoTra = ?",
                arrayOf(cCodigoTra.trim()),
                null,
                null,
                null*/
            )
            if (cursor != null && cursor.moveToFirst()) {
                val cCodigoUsuIndex = cursor.getColumnIndex("cCodigoUsu")
                val vNombreUsuIndex = cursor.getColumnIndex("vNombreUsu")
                /*val codigoTraIndex = cursor.getColumnIndex("cCodigoTra")
                val nombreTraIndex = cursor.getColumnIndex("vNombreTra")
                val apellidopatTraIndex = cursor.getColumnIndex("vApellidopatTra")
                val apellidomatTraIndex = cursor.getColumnIndex("vApellidomatTra")
                val codigoLugIndex = cursor.getColumnIndex("cCodigoLug")*/

                worker = WorkerModel(
                    cCodigoUsu = cursor.getString(cCodigoUsuIndex),
                    vNombreUsu = cursor.getString(vNombreUsuIndex)
                    /*cCodigoTra = cursor.getString(codigoTraIndex),
                    vNombreTra = cursor.getString(nombreTraIndex),
                    vApellidopatTra = cursor.getString(apellidopatTraIndex),
                    vApellidomatTra = cursor.getString(apellidomatTraIndex),
                    // Verificamos si el índice es válido antes de obtener el valor
                    cCodigoLug = if (codigoLugIndex != -1) cursor.getString(codigoLugIndex) else ""*/
                )
            }
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error al buscar trabajador: ${e.message}")
        } finally {
            cursor?.close()
        }
        return worker
    }

    fun debugAllWorkers(context: Context) {
        val db = this.readableDatabase
        // ¡CORREGIDO! Ahora consulta la tabla de trabajadores
        val cursor = db.rawQuery("SELECT * FROM nomtrabajador", null)

        if (cursor.moveToFirst()) {
            val codigoTraIndex = cursor.getColumnIndex("cCodigoTra")
            do {
                val codigo = cursor.getString(codigoTraIndex)
                android.util.Log.w("DB_DEBUG", "Registro en BD -> '$codigo'")
                Toast.makeText(context, "BD -> $codigo", Toast.LENGTH_SHORT).show()
            } while (cursor.moveToNext())
        } else {
            android.util.Log.w("DB_DEBUG", "La tabla nomtrabajador está vacía")
            Toast.makeText(context, "La tabla está vacía", Toast.LENGTH_SHORT).show()
        }
        cursor.close()
    }

    fun clearWorkers() {
        this.writableDatabase.delete("nomtrabajador", null, null)
    }

    // --- FUNCIONES DE REGISTROS DE TRANSPORTE ---
    fun insertTransportRecord(
        cCodigoTra: String,
        vChoferTrn: String,
        cFormaregTrn: String,
        dRegistroTrn: String,
        cTiporegTrn: String,
        cCodigoUsu: String,
        dCreacionTrn: String,
        cControlVeh: Int,
        cControlRut: Int,
        location: Location?, // Este es el parámetro faltante
        nCostoRut: Double
    ): Boolean {
        val db = this.writableDatabase
        // Define el formato de fecha y hora
        //val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale("es", "MX"))
        // Obtiene la fecha y hora actuales en el formato deseado
        //val currentDateTime = dateFormat.format(Date(System.currentTimeMillis()))


        //val formato  = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale("es", "MX"))
        //val currentDateTime = LocalDateTime.now().format(formato)

        val values = ContentValues().apply {
            put("cCodigoappTrn", 1)
            put("cCodigoTra", cCodigoTra)
            put("vChoferTrn", vChoferTrn)
            put("cFormaregTrn", cFormaregTrn)
            put("dRegistroTrn", dRegistroTrn)
            put("cTiporegTrn", cTiporegTrn)
            put("cLongitudTrn", "${location?.longitude?.toString()}" ?: "")
            put("cLatitudTrn", "${location?.latitude?.toString()}" ?: "")
            put("cAlturaTrn", "${location?.altitude?.toString()}" ?: "")
            put("cCodigoUsu", cCodigoUsu)
            put("dCreacionTrn", dCreacionTrn)
            put("cControlVeh", cControlVeh)
            put("cControlRut", cControlRut)
            put("nCostoRut", nCostoRut)
            put("cUsumodTrn", "")
            put("dModifiTrn", "")
        }
        Log.e("DatosDB", "datos insertados:$values" )

        return try {
            val result = db.insert("z_nomtransportes", null, values)
            db.close()
            result != -1L
            //false // Temporalmente deshabilitado para pruebas
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error al insertar registro de transporte: ${e.message}")
            false
        }
    }

    fun listUnsynchronizedRecords(): List<RecordModel>? {
        val db = this.readableDatabase

        val cursor = db.query(
            "z_nomtransportes", // Nombre de la tabla
            null, // Selecciona todas las columnas
            "isSynced = 0", // Condición de selección
            null,
            null,
            null,
            null,
            null
        )

        val records = mutableListOf<RecordModel>()

        try {
            if (cursor.moveToFirst()) {
                do {
                    val record = RecordModel(
                        cCodigoappTrn = cursor.getInt(cursor.getColumnIndexOrThrow("cCodigoappTrn")),
                        vChoferTrn = cursor.getString(cursor.getColumnIndexOrThrow("vChoferTrn")),
                        cCodigoTra = cursor.getString(cursor.getColumnIndexOrThrow("cCodigoTra")),
                        cFormaregTrn = cursor.getString(cursor.getColumnIndexOrThrow("cFormaregTrn")),
                        dRegistroTrn = cursor.getString(cursor.getColumnIndexOrThrow("dRegistroTrn")),
                        cTiporegTrn = cursor.getString(cursor.getColumnIndexOrThrow("cTiporegTrn")),
                        cLongitudTrn = cursor.getString(cursor.getColumnIndexOrThrow("cLongitudTrn")),
                        cLatitudTrn = cursor.getString(cursor.getColumnIndexOrThrow("cLatitudTrn")),
                        cAlturaTrn = cursor.getString(cursor.getColumnIndexOrThrow("cAlturaTrn")),
                        cCodigoUsu = cursor.getString(cursor.getColumnIndexOrThrow("cCodigoUsu")).uppercase(),
                        dCreacionTrn = cursor.getString(cursor.getColumnIndexOrThrow("dCreacionTrn")),
                        cControlVeh = cursor.getInt(cursor.getColumnIndexOrThrow("cControlVeh")),
                        cControlRut = cursor.getInt(cursor.getColumnIndexOrThrow("cControlRut")),
                        nCostoRut = cursor.getDouble(cursor.getColumnIndexOrThrow("nCostoRut")),
                        cUsumodTrn = cursor.getString(cursor.getColumnIndexOrThrow("cUsumodTrn")),
                        dModifiTrn = cursor.getString(cursor.getColumnIndexOrThrow("dModifiTrn")),
                        isSynced = cursor.getInt(cursor.getColumnIndexOrThrow("isSynced"))
                    )
                    records.add(record)
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor.close()
        }

        return if (records.isNotEmpty()) records else null
    }

    fun getUnsynchronizedRecords(): List<RecordModel>? {
        val db = this.readableDatabase

        val cursor = db.query(
            "z_nomtransportes",
            null,
            "isSynced = 0", // puedes agregar más condiciones aquí si quieres
            null,
            null,
            null,
            null,
            null
        )

        val records = mutableListOf<RecordModel>()

        try {
            if (cursor.moveToFirst()) {
                do {
                    val record = RecordModel(
                        cCodigoappTrn = cursor.getInt(cursor.getColumnIndexOrThrow("cCodigoappTrn")),
                        vChoferTrn = cursor.getString(cursor.getColumnIndexOrThrow("vChoferTrn")),
                        cCodigoTra = cursor.getString(cursor.getColumnIndexOrThrow("cCodigoTra")),
                        cFormaregTrn = cursor.getString(cursor.getColumnIndexOrThrow("cFormaregTrn")),
                        dRegistroTrn = cursor.getString(cursor.getColumnIndexOrThrow("dRegistroTrn")),
                        cTiporegTrn = cursor.getString(cursor.getColumnIndexOrThrow("cTiporegTrn")),
                        cLongitudTrn = cursor.getString(cursor.getColumnIndexOrThrow("cLongitudTrn")),
                        cLatitudTrn = cursor.getString(cursor.getColumnIndexOrThrow("cLatitudTrn")),
                        cAlturaTrn = cursor.getString(cursor.getColumnIndexOrThrow("cAlturaTrn")),
                        cCodigoUsu = cursor.getString(cursor.getColumnIndexOrThrow("cCodigoUsu")).uppercase(),
                        dCreacionTrn = cursor.getString(cursor.getColumnIndexOrThrow("dCreacionTrn")),
                        cControlVeh = cursor.getInt(cursor.getColumnIndexOrThrow("cControlVeh")),
                        cControlRut = cursor.getInt(cursor.getColumnIndexOrThrow("cControlRut")),
                        nCostoRut = cursor.getDouble(cursor.getColumnIndexOrThrow("nCostoRut")),
                        cUsumodTrn = cursor.getString(cursor.getColumnIndexOrThrow("cUsumodTrn")),
                        dModifiTrn = cursor.getString(cursor.getColumnIndexOrThrow("dModifiTrn")),
                        isSynced = cursor.getInt(cursor.getColumnIndexOrThrow("isSynced"))
                    )
                    records.add(record)
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor.close()
        }

        return if (records.isNotEmpty()) records else null
    }

    fun updateRecord(
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
    ): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("vChoferTrn", vChoferTrn)
            put("cCodigoTra", cCodigoTra)
            put("cFormaregTrn", cFormaregTrn)
            put("dRegistroTrn", dRegistroTrn)
            put("cTiporegTrn", cTiporegTrn)
            put("cLongitudTrn", cLongitudTrn)
            put("cLatitudTrn", cLatitudTrn)
            put("cAlturaTrn", cAlturaTrn)
            put("cCodigoUsu", cCodigoUsu)
            put("dCreacionTrn", dCreacionTrn)
            put("cControlVeh", cControlVeh)
            put("cControlRut", cControlRut)
            put("nCostoRut", nCostoRut)
            put("cUsumodTrn", cUsumodTrn)
            put("dModifiTrn", dModifiTrn)
            put("isSynced", isSynced)
        }

        // El WHERE clause es crucial para saber qué fila actualizar. Usamos cCodigoappTrn, que es tu identificador único.
        return try {
            db.update("z_nomtransportes", values, "cCodigoappTrn = ?", arrayOf(cCodigoappTrn.toString()))
        } catch (e: Exception) {
            Log.e("Database Error", "Error al actualizar registro de transporte: ${e.message}")
            0
        } finally {
            db.close()
        }
    }

    // DatabaseHelper.kt (Añadir al final de la clase)

    // --- FUNCIONES DE VALIDACIÓN Y CONTEO ---

    /**
     * Verifica si un trabajador tiene un registro de transporte en las últimas 2 horas.
     * @param workerCode El código del trabajador a validar.
     * @param minTimeMillis El intervalo de tiempo mínimo para re-escaneo (debe ser 2 * 60 * 60 * 1000L).
     * @return El String de la última hora de registro o null si puede ser escaneado.
     */
    fun getLatestTransportRecordTime(workerCode: String): String? {
        val db = this.readableDatabase
        var lastRecordTime: String? = null
        var cursor: Cursor? = null

        try {
            // Consulta para obtener el registro más reciente de ese trabajador
            val query = """
                SELECT dRegistroTrn
                FROM z_nomtransportes
                WHERE cCodigoTra = ?
                ORDER BY dRegistroTrn DESC
                LIMIT 1
            """
            cursor = db.rawQuery(query, arrayOf(workerCode))

            if (cursor != null && cursor.moveToFirst()) {
                lastRecordTime = cursor.getString(0)
            }
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error al obtener el último registro: ${e.message}")
        } finally {
            cursor?.close()
        }
        return lastRecordTime
    }

    /**
     * Cuenta cuántos trabajadores únicos han sido registrados en el día de hoy.
     * Esto asume que dRegistroTrn tiene el formato 'yyyy-MM-dd HH:mm:ss.SSS'.
     */
    fun getTodayUniqueWorkersCount(): Int {
        val db = this.readableDatabase
        val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        var count = 0
        var cursor: Cursor? = null

        try {
            // Consulta para contar distintos trabajadores cuya fecha de registro (parte de la cadena) sea hoy
            val query = """
                SELECT COUNT(DISTINCT cCodigoTra)
                FROM z_nomtransportes
                WHERE dRegistroTrn LIKE '$todayDate%' AND isSynced = 0
            """
            cursor = db.rawQuery(query, null)

            if (cursor.moveToFirst()) {
                count = cursor.getInt(0)
            }
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error al contar trabajadores de hoy: ${e.message}")
        } finally {
            cursor?.close()
        }
        return count
    }

    fun getSubidaBajada(workerCode: String): String? {
        val db = this.readableDatabase
        // ⭐ DECLARACIÓN DE FECHA ORIGINAL:
        val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        var count = 0
        var cursor: Cursor? = null

        try {
            // --- 1. Consulta para CONTAR REGISTROS de ese trabajador HOY ---
            val query = """
            SELECT COUNT(cCodigoTra)
            FROM z_nomtransportes
            WHERE dRegistroTrn LIKE ? || '%' 
            AND cCodigoTra = ? 
            -- Si quieres incluir los sincronizados (isSynced = 1), elimina la siguiente línea:
            AND cTiporegTrn = "0"
        """

            // Pasamos la fecha de hoy y el código del trabajador como argumentos
            cursor = db.rawQuery(query, arrayOf(todayDate, workerCode))

            if (cursor.moveToFirst()) {
                // Obtener el conteo
                count = cursor.getInt(0)
                Log.d("DB_SUBIDA_BAJADA", "Trabajador $workerCode tiene $count registros hoy sin sincronizar.")
            }
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error al contar registros: ${e.message}")
            count = 0
        } finally {
            cursor?.close()
        }

        // --- 2. Devolver "1" o "0" (String) ---
        // Si count > 0, el trabajador ya tiene un registro hoy (existe) = "1".
        // Si count = 0, no tiene registro hoy = "0".
        return if (count > 0) "1" else "0"
    }
}