package com.example.quickhelp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.quickhelp.R
import com.example.quickhelp.databinding.FragmentPerfilBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class PerfilFragment : Fragment() {

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        auth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()

        setupUserInfo()
        setupClickListeners()
        setupSwitches()

        return binding.root
    }

    private fun setupUserInfo() {
        val user = auth.currentUser
        user?.let {
            // Información básica
            binding.tvUserName.text = it.displayName ?: "Usuario QuickHelp"
            binding.tvUserEmail.text = it.email ?: "No especificado"
            binding.tvUserId.text = it.uid.take(8) + "..." // Mostrar solo parte del ID

            // Estadísticas (puedes personalizar estos datos)
            binding.tvAlertsCount.text = "12" // Ejemplo
            binding.tvTrustLevel.text = "95%"

            // Fecha de creación de la cuenta
            val creationDate = it.metadata?.creationTimestamp ?: System.currentTimeMillis()
            val sdf = SimpleDateFormat("MMM yyyy", Locale.getDefault())
            binding.tvMemberSince.text = sdf.format(Date(creationDate))

            // Cargar datos adicionales de Firestore si los tienes
            loadAdditionalUserData(it.uid)
        } ?: run {
            // Si no hay usuario, mostrar valores por defecto
            binding.tvUserName.text = "Invitado"
            binding.tvUserEmail.text = "No autenticado"
            binding.tvUserId.text = "--------"
        }
    }

    private fun loadAdditionalUserData(userId: String) {
        firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Cargar datos personalizados desde Firestore
                    val alertsCount = document.getLong("alertsCount") ?: 0
                    val trustLevel = document.getLong("trustLevel") ?: 95

                    binding.tvAlertsCount.text = alertsCount.toString()
                    binding.tvTrustLevel.text = "$trustLevel%"
                }
            }
            .addOnFailureListener {
                // Silencioso, usar valores por defecto
            }
    }

    private fun setupClickListeners() {
        binding.btnLogout.setOnClickListener {
            logoutUser()
        }

        // Puedes agregar más listeners para otras opciones
        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(
                requireContext(),
                "Notificaciones ${if (isChecked) "activadas" else "desactivadas"}",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(
                requireContext(),
                "Tema ${if (isChecked) "oscuro" else "claro"}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setupSwitches() {
        // Configurar estado inicial de los switches
        binding.switchNotifications.isChecked = true
        binding.switchDarkMode.isChecked = true
    }

    private fun logoutUser() {
        auth.signOut()
        Toast.makeText(requireContext(), "Sesión cerrada exitosamente", Toast.LENGTH_SHORT).show()

        // Navegar al login
        findNavController().navigate(R.id.loginFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}