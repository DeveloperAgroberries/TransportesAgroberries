package com.AgroberriesMX.transportesagroberries.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context):SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object{
        private const val DATABASE_NAME = "agrotrasnport.db"
        private const val DATABASE_VERSION = 1

        private const val CREATE_TABLE_LOGINS = """
            CREATE TABLE genlogin (
                controlLog INTEGER PRIMARY KEY AUTOINCREMENT,
                cCodigoUsu TEXT,
                vNombreUsu TEXT,
                cCodigoUsu TEXT
            )
        """

        private const val CREATE_TABLE_ROUTES = """
            CREATE TABLE z_nomruta (
                controlLog INTEGER PRIMARY KEY AUTOINCREMENT,
                cControlRut TEXT,
                vDescripcionRut TEXT
            )
        """

        private const val CREATE_TABLE_VEHICLES = """
            CREATE TABLE z_nomvehiculo (
                controlLog INTEGER PRIMARY KEY AUTOINCREMENT,
                cPlacaVeh TEXT,
                cControlPrv INTEGER
            )
        """

        private const val CREATE_TABLE_WORKERS = """
            CREATE TABLE nomtrabajador (
                contorlLog INTEGER PRIMARY KEY AUTOINCREMENT,
                cCodigoTra TEXT,
                cCodigoLug TEXT,
                vNombreTra TEXT,
                vApellidopatTra TEXT,
                vApellidomatTra TEXT
            )
        """

        private const val CREATE_TABLE_REGISTERS = """
            CREATE TABLE z_nomtransportes (
                controlLog INTEGER PRIMARY KEY AUTOINCREMENT,
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
                cControlVeh TEXT,
                cControlRut
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

}