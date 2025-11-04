package com.AgroberriesMX.transportesagroberries.ui.login

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.AgroberriesMX.transportesagroberries.databinding.ActivityLoginBinding
import com.AgroberriesMX.transportesagroberries.ui.home.MainActivity
import com.AgroberriesMX.transportesagroberries.ui.privacypolicy.PrivacyPolicyViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionPrefs: SharedPreferences
    private lateinit var persistentPrefs: SharedPreferences
    private val loginViewModel: LoginViewModel by viewModels()
    private val privatePolicyViewModel: PrivacyPolicyViewModel by viewModels()
    private var currentUser: String? = null

    companion object {
        private const val SESSION_PREFERENCES_KEY = "session_prefs"
        private const val PERSISTENT_PREFERENCES_KEY = "persistent_prefs"
        private const val PRIVATE_ACCESS_TOKEN_KEY = "access_token"
        private const val LOGGED_IN_KEY = "logged_in"
        private const val SYNCHRONIZED_CATALOGS_KEY = "synchronized_catalogs"
        private const val TAG = "LoginActivity"
        private const val REMIND_USERNAME_KEY = "Username"
        private const val REMIND_PASSWORD_KEY = "Password"
    }

    //TODO: Change the synchronize catalogs for the first time in the login only for the first access
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
        observeViewModel()
    }

    private fun initUI() {
        initListeners()
        loadUserDataIfExists()
    }

    private fun initListeners() {

        binding.btnLogin.setOnClickListener {
            val user = binding.etUser.text.toString().uppercase().trim()
            val password = binding.etPassword.text.toString().trim()
            currentUser = user // Guarda el usuario en la variable de clase

            if (user != "" || password != "") {
                lifecycleScope.launch {
                    if(binding.cbReminder.isChecked){
                        remindUser(user,password)
                    }else{
                        clearUserData()
                    }
                    loginViewModel.login(user, password, "1", "")
                }
            } else {
                Toast.makeText(
                    this,
                    "El usuario o la contraseña estan vacios, vuelve a intentarlo, por favor.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun loadUserDataIfExists() {
        persistentPrefs = getSharedPreferences(PERSISTENT_PREFERENCES_KEY, MODE_PRIVATE)
        val savedUsername = persistentPrefs.getString(REMIND_USERNAME_KEY, null)
        val savedPassword = persistentPrefs.getString(REMIND_PASSWORD_KEY, null)

        // Si se han guardado los datos, colócalos en los campos de texto correspondientes
        if (savedUsername != null && savedPassword != null) {
            binding.etUser.setText(savedUsername)
            binding.etPassword.setText(savedPassword)
            binding.cbReminder.isChecked = true // Marca el checkbox si se encontraron datos
        }
    }

    private fun observeViewModel() {
        loginViewModel.state.observe(this, Observer { state ->
            when (state) {
                is LoginState.Waiting -> {
                    binding.pb.visibility = View.GONE
                }

                is LoginState.Loading -> {
                    binding.pb.visibility = View.VISIBLE
                    binding.etUser.isEnabled = false
                    binding.etPassword.isEnabled = false
                    binding.btnLogin.isEnabled = false
                }

                is LoginState.Success -> {
                    binding.pb.visibility = View.GONE
                    binding.etUser.isEnabled = true // Habilitar campos y botón al finalizar carga
                    binding.etPassword.isEnabled = true
                    binding.btnLogin.isEnabled = true

                    if(state.isLocal){
                        Toast.makeText(applicationContext, "Inicio de sesión: ¡Acceso offline!", Toast.LENGTH_LONG).show()
                        saveUserCode(binding.etUser.text.toString().trim())
                        navigateToMainActivity()
                    }else{
                        val token = state.success?.token // Suponiendo que 'state' tiene un campo 'token'

                        if (token != null) {
                            Toast.makeText(applicationContext, "Inicio de sesión: ¡Acceso online!", Toast.LENGTH_LONG).show()
                            saveToken(token) // Guarda el token del servidor
                            saveUserCode(binding.etUser.text.toString().trim())
                            lifecycleScope.launch {
                                try {
                                    synchronizeCatalogs() // Intenta sincronizar porque hay conexión
                                    navigateToMainActivity()
                                } catch (e: java.lang.Exception) {
                                    Toast.makeText(applicationContext, "Error en la sincronización: ${e.message}", Toast.LENGTH_LONG).show()
                                    // A pesar del error de sincronización, el usuario ya inició sesión
                                    navigateToMainActivity()
                                }
                            }
                        } else {
                            // Esto no debería ocurrir si el ViewModel maneja correctamente 'isLocal'
                            Toast.makeText(applicationContext, "Error: Token nulo en acceso online exitoso.", Toast.LENGTH_LONG).show()
                            // Considera qué hacer aquí: ¿volver a login, mostrar error crítico?
                            navigateToMainActivity() // Por ahora, navega para no bloquear al usuario
                        }
                        // Asegúrate de que el usuario no sea nulo antes de guardar
                        /*currentUser?.let { userCode ->
                            saveUserCode(userCode)
                        }

                        saveToken(token)
                        lifecycleScope.launch {
                            synchronizeCatalogs()
                        }
                        navigateToMainActivity()*/
                    }
                }

                is LoginState.Error -> {
                    binding.pb.visibility = View.GONE
                    binding.etUser.isEnabled = true
                    binding.etPassword.isEnabled = true
                    binding.btnLogin.isEnabled = true
                    Toast.makeText(this, "${state.message}", Toast.LENGTH_LONG).show()
                }

                else -> {}
            }
        })

        loginViewModel.state.observe(this, Observer { state ->
            when (state) {
                is LoginState.Waiting -> {
                    binding.pb.visibility = View.GONE
                }

                is LoginState.Loading -> {
                    binding.pb.visibility = View.VISIBLE
                    binding.etUser.isEnabled = false
                    binding.etPassword.isEnabled = false
                    binding.btnLogin.isEnabled = false
                }

                /*is LoginState.Success -> {
                    binding.pb.visibility = View.GONE
                    val token = state.success.token // Suponiendo que 'state' tiene un campo 'token'
                    saveToken(token)
                    lifecycleScope.launch {
                        synchronizeCatalogs()
                    }
                    navigateToMainActivity()
                }*/

                is LoginState.Error -> {
                    binding.pb.visibility = View.GONE
                    binding.etUser.isEnabled = true
                    binding.etPassword.isEnabled = true
                    binding.btnLogin.isEnabled = true
                    Toast.makeText(this, "${state.message}", Toast.LENGTH_LONG).show()
                }

                else -> {}
            }
        })
    }

    private fun saveToken(token: String) {
        sessionPrefs = getSharedPreferences(SESSION_PREFERENCES_KEY, MODE_PRIVATE)
        with(sessionPrefs.edit()) {
            putString(PRIVATE_ACCESS_TOKEN_KEY, "Bearer ${token}")
            apply()
        }
    }

    private fun remindUser(usr: String, pwd: String) {
        persistentPrefs = getSharedPreferences(PERSISTENT_PREFERENCES_KEY, MODE_PRIVATE)
        with(persistentPrefs.edit()){
            putString(REMIND_USERNAME_KEY, usr)
            putString(REMIND_PASSWORD_KEY, pwd)
            apply()
        }
    }

    private fun clearUserData() {
        persistentPrefs = getSharedPreferences(PERSISTENT_PREFERENCES_KEY, MODE_PRIVATE)
        with(persistentPrefs.edit()) {
            remove(REMIND_USERNAME_KEY)
            remove(REMIND_PASSWORD_KEY)
            apply()
        }
    }

    private suspend fun synchronizeCatalogs() {
        persistentPrefs = getSharedPreferences(PERSISTENT_PREFERENCES_KEY, MODE_PRIVATE)
        sessionPrefs = getSharedPreferences(SESSION_PREFERENCES_KEY, MODE_PRIVATE)
        val token = sessionPrefs.getString(PRIVATE_ACCESS_TOKEN_KEY,"NO hay token")
        withContext(Dispatchers.IO){
            try {
                privatePolicyViewModel.dataResponse(token.toString())
            }catch (e: Exception){
                Log.e(TAG, "Hubo un error obteniendo e insertando los datos en la base de datos: ${e.message}")
            }
        }

        with(persistentPrefs.edit()) {
            putBoolean(SYNCHRONIZED_CATALOGS_KEY, true)
            apply()
        }
    }

    private fun navigateToMainActivity() {
        sessionPrefs = getSharedPreferences(SESSION_PREFERENCES_KEY, MODE_PRIVATE)

        with(sessionPrefs.edit()) {
            putBoolean(LOGGED_IN_KEY, true)
            apply()
        }

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun saveUserCode(userCode: String) {
        sessionPrefs = getSharedPreferences(SESSION_PREFERENCES_KEY, MODE_PRIVATE)
        with(sessionPrefs.edit()) {
            putString("cCodigoUsu", userCode)
            apply()
        }
    }
}