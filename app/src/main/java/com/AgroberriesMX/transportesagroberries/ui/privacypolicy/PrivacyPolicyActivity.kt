package com.AgroberriesMX.transportesagroberries.ui.privacypolicy

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.CAMERA
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.AgroberriesMX.transportesagroberries.databinding.ActivityPrivacyPolicyBinding
import com.AgroberriesMX.transportesagroberries.ui.home.MainActivity

class PrivacyPolicyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrivacyPolicyBinding

    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
        private const val FINE_LOCATION_PERMISSION_CODE = 101
        private const val COARSE_LOCATION_PERMISSION_CODE = 102
        private const val BACKGROUND_LOCATION_PERMISSION_CODE = 103
        private const val PREFERENCES_KEY = "app_preferences"
        private const val POLICIES_SHOWN_KEY = "policies_shown"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacyPolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
    }

    private fun initUI() {
        initListeners()
    }

    private fun initListeners() {
        binding.btnPermissions.setOnClickListener {
            val sharedPreferences = getSharedPreferences(PREFERENCES_KEY, MODE_PRIVATE)
            val permissions = listOf(
                Pair(ACCESS_FINE_LOCATION, FINE_LOCATION_PERMISSION_CODE)
            )

            with(sharedPreferences.edit()) {
                putBoolean(POLICIES_SHOWN_KEY, true)
                apply()
            }

            for (permission in permissions) {
                checkPermission(permission.first, permission.second)
            }

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()

        }

        binding.btnPermissions.setOnClickListener {
            val sharedPreferences = getSharedPreferences(PREFERENCES_KEY, MODE_PRIVATE)

            with(sharedPreferences.edit()){
                putBoolean(POLICIES_SHOWN_KEY, true)
                apply()
            }

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_DENIED
        ) {
            // Requesting the permission
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == FINE_LOCATION_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permisos de ubicación otorgados", Toast.LENGTH_LONG)
                    .show()
            } else {
                Toast.makeText(this, "Permisos de ubicación denegados", Toast.LENGTH_LONG)
                    .show()
            }
        } else if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permisos de camara otorgados", Toast.LENGTH_LONG)
                    .show()
            } else {
                Toast.makeText(this, "Permisos de camara denegados", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }
}