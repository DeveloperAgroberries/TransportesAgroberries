package com.AgroberriesMX.transportesagroberries.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.AgroberriesMX.transportesagroberries.R
import com.AgroberriesMX.transportesagroberries.databinding.ActivityMainBinding
import com.AgroberriesMX.transportesagroberries.ui.login.LoginActivity
import com.AgroberriesMX.transportesagroberries.ui.privacypolicy.PrivacyPolicyActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController

    private lateinit var appBarConfiguration: AppBarConfiguration

    companion object {
        private const val PREFERENCES_KEY = "app_preferences"
        private const val POLICIES_SHOWN_KEY = "policies_shown"
        private const val LOGGED_IN_KEY = "logged_in"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
    }

    private fun initUI() {
        appRun()
        initListener()
        initNavigation()
    }

    private fun appRun() {
        val sharedPreferences = getSharedPreferences(PREFERENCES_KEY, MODE_PRIVATE)
        val policiesShown = sharedPreferences.getBoolean(POLICIES_SHOWN_KEY, false)
        val loggedIn = sharedPreferences.getBoolean(LOGGED_IN_KEY, false)

        when {
            !policiesShown -> {
                val intent = Intent(this, PrivacyPolicyActivity::class.java)
                startActivity(intent)
                finish()
            }
            !loggedIn -> {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            else -> {
                setContentView(R.layout.activity_main)
            }
        }
    }

    private fun initListener() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackFunction()
            }
        })
    }

    private fun initNavigation() {
        setSupportActionBar(binding.toolbar)

        val navHost =
            supportFragmentManager.findFragmentById(binding.fragmentContainerView.id) as NavHostFragment
        navController = navHost.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navDriverData,
                R.id.navSync,
                R.id.navAbout,
                R.id.navLogout,
            ), binding.drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

//        binding.navView.setNavigationItemSelectedListener { menuItem ->
//            when (menuItem.itemId) {
//                R.id.navLogout -> {
//                    // Lógica para logout
//                    handleLogout()
//                    true
//                }
//                else -> false
//            }
//        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun onBackFunction() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }

//    private fun handleLogout() {
//        // Aquí puedes agregar la lógica para el logout
//        // Por ejemplo, limpiar las preferencias compartidas y regresar a la pantalla de login
//        val sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)
//        val editor = sharedPreferences.edit()
//        editor.clear()
//        editor.apply()
//
//        // Lanzar la actividad de login
////        val intent = Intent(this, LoginActivity::class.java)
////        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
////        startActivity(intent)
////        finish()
//    }
}
