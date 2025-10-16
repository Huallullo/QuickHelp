package com.example.quickhelp.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.quickhelp.R
import com.example.quickhelp.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()

        setupClickListeners()

        return binding.root
    }

    private fun setupClickListeners() {
        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            if (validateInputs(name, email, password, confirmPassword)) {
                registerUser(name, email, password)
            }
        }

        binding.btnGoToLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    private fun validateInputs(name: String, email: String, password: String, confirmPassword: String): Boolean {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password != confirmPassword) {
            Toast.makeText(requireContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.length < 6) {
            Toast.makeText(requireContext(), "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun registerUser(name: String, email: String, password: String) {

        binding.btnRegister.isEnabled = false

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Actualizar perfil del usuario con el nombre
                    val user = auth.currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()

                    user?.updateProfile(profileUpdates)?.addOnCompleteListener { profileTask ->

                        binding.btnRegister.isEnabled = true

                        if (profileTask.isSuccessful) {
                            Toast.makeText(requireContext(), "Registro exitoso", Toast.LENGTH_SHORT).show()
                            // Navegar al flujo principal
                            findNavController().navigate(R.id.action_loginFragment_to_mainFlow)
                        } else {
                            Toast.makeText(requireContext(), "Error al actualizar perfil", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {

                    binding.btnRegister.isEnabled = true
                    Toast.makeText(requireContext(), "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}