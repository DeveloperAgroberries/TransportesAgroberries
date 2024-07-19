package com.AgroberriesMX.transportesagroberries.ui.login

sealed class LoginState {
    data object Loading:LoginState()
    data class Error(val error:String):LoginState()
    data class Success(val success:String):LoginState()
}