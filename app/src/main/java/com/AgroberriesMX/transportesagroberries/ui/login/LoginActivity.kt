package com.AgroberriesMX.transportesagroberries.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.voice.VoiceInteractionSession.ActivityId
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.AgroberriesMX.transportesagroberries.R
import com.AgroberriesMX.transportesagroberries.databinding.ActivityLoginBinding
import com.AgroberriesMX.transportesagroberries.ui.home.MainActivity
import com.AgroberriesMX.transportesagroberries.ui.privacypolicy.PrivacyPolicyActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()

    companion object {
        private const val PREFERENCES_KEY = "app_preferences"
        private const val PRIVATE_ACCESS_TOKEN_KEY = "access_token"
        private const val LOGGED_IN_KEY = "logged_in"
        private const val SYNCHRONIZED_CATALOGS_KEY = "synchronized_catalogs"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
        observeViewModel()
    }

    private fun initUI() {
        initListeners()
    }

    private fun initListeners() {

        binding.btnLogin.setOnClickListener {
            val user = binding.etUser.text.toString().uppercase().trim()
            val password = binding.etPassword.text.toString().trim()

            if( user != "" || password != ""){
                lifecycleScope.launch {
                    loginViewModel.login(user, password, "1","")
                }
            }else{
                Toast.makeText(this, "El usuario o la contraseña estan vacios, vuelve a intentarlo, por favor.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun observeViewModel() {
        loginViewModel.state.observe(this, Observer { state ->
            when (state) {
                is LoginState.Waiting -> {
                    binding.pb.isVisible=false
                }

                is LoginState.Loading -> {
                    binding.pb.isVisible = true
                }

                is LoginState.Success -> {
                    binding.pb.isVisible = false
                    val token = state.success.token // Suponiendo que 'state' tiene un campo 'token'
                    saveToken(token)
                    synchronizeCatalogs()
                    navigateToMainActivity()
                }

                is LoginState.Error -> {
                    Toast.makeText(this, "${state.message}", Toast.LENGTH_LONG).show()
                }

                else -> {}
            }
        })
    }

    private fun saveToken(token: String) {
        val sharedPreferences = getSharedPreferences(PREFERENCES_KEY, MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString(PRIVATE_ACCESS_TOKEN_KEY, token)
            apply()
        }
    }

    private fun synchronizeCatalogs() {
        val sharedPreferences = getSharedPreferences(PREFERENCES_KEY, MODE_PRIVATE)



        with(sharedPreferences.edit()) {
            putBoolean(SYNCHRONIZED_CATALOGS_KEY, true)
            apply()
        }
    }

    private fun navigateToMainActivity() {
        val sharedPreferences = getSharedPreferences(PREFERENCES_KEY, MODE_PRIVATE)

        with(sharedPreferences.edit()) {
            putBoolean(LOGGED_IN_KEY, true)
            apply()
        }

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}