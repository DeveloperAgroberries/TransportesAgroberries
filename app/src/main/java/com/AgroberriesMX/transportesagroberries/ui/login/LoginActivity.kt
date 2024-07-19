package com.AgroberriesMX.transportesagroberries.ui.login

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.AgroberriesMX.transportesagroberries.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
    }

    private fun initUI() {
        initListeners()
    }

    private fun initListeners() {
        binding.btnLogin.setOnClickListener {
            val user = binding.etUser.text.toString()
            val password = binding.etPassword.text.toString()

            if( user != "" || password != ""){
//                user.uppercase()
//                password.trim()
            }else{
                Toast.makeText(this, "El usuario o la contraseña estan vacios, vuelve a intentarlo, por favor.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}