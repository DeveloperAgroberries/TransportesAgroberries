package com.AgroberriesMX.transportesagroberries.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.AgroberriesMX.transportesagroberries.data.network.request.LoginRequest
import com.AgroberriesMX.transportesagroberries.domain.model.TokenModel
import com.AgroberriesMX.transportesagroberries.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val getLoginUseCase: LoginUseCase) : ViewModel() {
    private var _state = MutableLiveData<LoginState>(LoginState.Waiting)
    val state: LiveData<LoginState> = _state

    lateinit var tokenModel: TokenModel

    fun login(userId: String, password: String, activeUser: String, creatorId: String) {
        viewModelScope.launch {
            _state.value = LoginState.Loading
            try {
                val loginRequest = LoginRequest(userId, password, activeUser, creatorId)
                val response = getLoginUseCase(loginRequest)
                if (response != null) {
                    tokenModel = response
                    _state.value = LoginState.Success(response)
                } else {
                    _state.value = LoginState.Error("Fallo el acceso")
                }
            } catch (e: Exception) {
                _state.value = LoginState.Error(e.message ?: "A ocurrido un error")
            }
        }
    }
}