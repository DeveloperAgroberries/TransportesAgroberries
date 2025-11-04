package com.AgroberriesMX.transportesagroberries.data.repository

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalAuthRepository @Inject constructor(@ApplicationContext context: Context) {

    // Usaremos un archivo privado de SharedPreferences
    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    private val KEY_USER = "last_user_id"
    private val KEY_PASS = "last_password"

    /**
     * Guarda el último usuario y contraseña al tener un login ONLINE exitoso.
     * Solo guarda el usuario limpio (sin trim).
     */
    fun saveCredentials(userId: String, password: String) {
        prefs.edit()
            .putString(KEY_USER, userId.trim()) // Limpiamos y guardamos
            .putString(KEY_PASS, password)
            .apply()
    }

    /**
     * Valida las credenciales ingresadas por el usuario contra las guardadas localmente.
     * Se usa cuando NO hay conexión a la red.
     */
    fun validateCredentials(userId: String, password: String): Boolean {
        val storedUser = prefs.getString(KEY_USER, null)
        val storedPass = prefs.getString(KEY_PASS, null)

        // Compara las credenciales ingresadas (limpiando el user) con las guardadas.
        return (userId.trim() == storedUser) && (password == storedPass)
    }

    /**
     * Opcional: Para el logout.
     */
    fun clearCredentials() {
        prefs.edit().clear().apply()
    }
}