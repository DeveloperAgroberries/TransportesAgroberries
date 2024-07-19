package com.AgroberriesMX.transportesagroberries.ui.privacypolicy

import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.CAMERA
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.AgroberriesMX.transportesagroberries.databinding.ActivityPrivacyPolicyBinding

class PrivacyPolicyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrivacyPolicyBinding

    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
        private const val FINE_LOCATION_PERMISSION_CODE = 101
        private const val COARSE_LOCATION_PERMISSION_CODE = 102
        private const val BACKGROUND_LOCATION_PERMISSION_CODE = 103
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
//        binding.btnPermissions?.setOnClickListener {
//            checkPermission(
//                CAMERA,
//                CAMERA_PERMISSION_CODE
//            )
//        }
//
//        binding.btnPermissions?.setOnClickListener {
//            checkPermission(
//                ACCESS_FINE_LOCATION,
//                FINE_LOCATION_PERMISSION_CODE
//            )
//        }
//
//        binding.btnPermissions?.setOnClickListener {
//            checkPermission(
//                ACCESS_COARSE_LOCATION,
//                COARSE_LOCATION_PERMISSION_CODE
//            )
//        }
//
//        binding.btnPermissions?.setOnClickListener {
//            checkPermission(
//                ACCESS_BACKGROUND_LOCATION,
//                BACKGROUND_LOCATION_PERMISSION_CODE
//            )
//        }

        initSaveState()
    }

    private fun initSaveState() {
        val sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)

        with(sharedPreferences.edit()) {
            putBoolean("policies_shown", true)
            apply()
        }
        // Volver a la MainActivity
//        finish()
    }

    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_DENIED
        ) {
            // Requesting the permission
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
            Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permisos de camara otorgados", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(this, "Permisos de camara denegados", Toast.LENGTH_SHORT)
                    .show()
            }
        } else if (requestCode == FINE_LOCATION_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permisos de ubicacion otorgados", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(this, "Permisos de ubicacion denegados", Toast.LENGTH_SHORT)
                    .show()
            }
        } else if (requestCode == COARSE_LOCATION_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permisos de ubicacion otorgados", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(this, "Permisos de ubicacion denegados", Toast.LENGTH_SHORT)
                    .show()
            }
        } else if (requestCode == BACKGROUND_LOCATION_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permisos de ubicacion otorgados", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(this, "Permisos de ubicacion denegados", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}