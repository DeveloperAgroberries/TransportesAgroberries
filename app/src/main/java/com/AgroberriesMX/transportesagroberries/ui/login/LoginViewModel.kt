package com.AgroberriesMX.transportesagroberries.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.AgroberriesMX.transportesagroberries.data.network.request.LoginRequest
import com.AgroberriesMX.transportesagroberries.data.repository.LocalAuthRepository // Importar Repositorio Local
import com.AgroberriesMX.transportesagroberries.domain.model.TokenModel
import com.AgroberriesMX.transportesagroberries.domain.usecase.LoginUseCase
import com.AgroberriesMX.transportesagroberries.utils.NetworkUtils // Importar Utilidad de Red
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val getLoginUseCase: LoginUseCase,
    private val networkUtils: NetworkUtils, // ⭐️ Añadir inyección
    private val localAuthRepository: LocalAuthRepository // ⭐️ Añadir inyección
) : ViewModel() {

    private var _state = MutableLiveData<LoginState>(LoginState.Waiting)
    // Asegúrate de que LoginState.Success tiene el flag isLocal:
    // data class Success(val success: TokenModel?, val isLocal: Boolean) : LoginState()
    val state: LiveData<LoginState> = _state

    lateinit var tokenModel: TokenModel

    fun login(userId: String, password: String, activeUser: String, creatorId: String) {
        viewModelScope.launch {
            _state.value = LoginState.Loading

            val user = userId.trim() // Limpiamos espacios en el usuario
            val pass = password

            if (networkUtils.isNetworkAvailable()) {
                // --- 1. INTENTO ONLINE (Hay Conexión) ---
                try {
                    val loginRequest = LoginRequest(user, pass, activeUser, creatorId)
                    val response = getLoginUseCase(loginRequest)

                    if (response != null) {
                        // Éxito ONLINE: Guardamos las credenciales para el futuro acceso offline
                        localAuthRepository.saveCredentials(user, pass)
                        tokenModel = response
                        _state.value = LoginState.Success(response, isLocal = false) // isLocal = false
                        return@launch
                    }
                    // Si la respuesta es nula, intentamos fallback local.
                    attemptLocalLogin(user, pass)

                } catch (e: Exception) {
                    // Si la red falla (Timeout, 401, 500), intentamos el fallback local
                    attemptLocalLogin(user, pass)
                }
            } else {
                // --- 2. SIN CONEXIÓN: INTENTO OFFLINE DIRECTO ---
                attemptLocalLogin(user, pass)
            }
        }
    }

    // Método privado para manejar la lógica de login local
    private fun attemptLocalLogin(userId: String, password: String) {
        if (localAuthRepository.validateCredentials(userId, password)) {
            // Éxito Offline: Enviamos null como token pero isLocal = true
            _state.value = LoginState.Success(null, isLocal = true)
        } else {
            // Fallo definitivo
            _state.value = LoginState.Error("Fallo de conexión y credenciales locales inválidas.")
        }
    }
}