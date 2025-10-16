package com.example.quickhelp

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.quickhelp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Verificar usuario actual (solo para log)
        val currentUser = auth.currentUser
        if (currentUser != null) {
            Log.d("MainActivity", "✅ Usuario autenticado: ${currentUser.email}")
        } else {
            Log.d("MainActivity", "👤 No hay usuario autenticado, mostrando login")
        }

        setupNavigation()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Conectar BottomNavigation con NavController
        binding.bottomNavigationView.setupWithNavController(navController)

        // Ocultar BottomNavigation en pantallas de auth
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment, R.id.registerFragment -> {
                    binding.bottomNavigationView.visibility = View.GONE
                    supportActionBar?.hide()
                }
                else -> {
                    binding.bottomNavigationView.visibility = View.VISIBLE
                    supportActionBar?.show()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        return navHostFragment.navController.navigateUp() || super.onSupportNavigateUp()
    }
}