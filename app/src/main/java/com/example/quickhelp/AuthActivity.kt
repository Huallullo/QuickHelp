package com.example.quickhelp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.quickhelp.databinding.ActivityAuthBinding
import com.google.firebase.auth.FirebaseAuth

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // 🚀 Configura el NavHost para los fragmentos de Login y Register
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_auth) as NavHostFragment
        val navController = navHostFragment.navController

        // ❌ NO usamos ActionBar aquí, así evitamos el error
        // NavigationUI.setupActionBarWithNavController(this, navController)
    }

    override fun onStart() {
        super.onStart()

        // ✅ Si el usuario ya inició sesión, lo enviamos directo al MainActivity
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
