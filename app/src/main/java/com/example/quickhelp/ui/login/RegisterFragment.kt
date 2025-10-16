package com.example.quickhelp.ui.login

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.quickhelp.R
import com.example.quickhelp.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        auth = Firebase.auth

        setupClickListeners()
        setupPasswordToggles()

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
            // Navegar al login
            findNavController().navigate(R.id.loginFragment)
        }
    }

    private fun setupPasswordToggles() {
        // Toggle para contraseña
        binding.textInputLayoutPassword.setEndIconOnClickListener {
            togglePasswordVisibility(binding.etPassword, binding.textInputLayoutPassword)
        }

        // Toggle para confirmar contraseña
        binding.textInputLayoutConfirmPassword.setEndIconOnClickListener {
            togglePasswordVisibility(binding.etConfirmPassword, binding.textInputLayoutConfirmPassword)
        }
    }

    private fun togglePasswordVisibility(
        editText: com.google.android.material.textfield.TextInputEditText,
        textInputLayout: com.google.android.material.textfield.TextInputLayout
    ) {
        if (editText.transformationMethod is PasswordTransformationMethod) {
            // Mostrar contraseña
            editText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            textInputLayout.endIconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_visibility)
        } else {
            // Ocultar contraseña
            editText.transformationMethod = PasswordTransformationMethod.getInstance()
            textInputLayout.endIconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_visibility_off)
        }
        editText.setSelection(editText.text?.length ?: 0)
    }

    private fun validateInputs(name: String, email: String, password: String, confirmPassword: String): Boolean {
        if (name.isEmpty()) {
            showError("El nombre es requerido")
            return false
        }

        if (email.isEmpty()) {
            showError("El correo electrónico es requerido")
            return false
        }

        if (password.isEmpty()) {
            showError("La contraseña es requerida")
            return false
        }

        if (password.length < 6) {
            showError("La contraseña debe tener al menos 6 caracteres")
            return false
        }

        if (confirmPassword.isEmpty()) {
            showError("Confirma tu contraseña")
            return false
        }

        if (password != confirmPassword) {
            showError("Las contraseñas no coinciden")
            return false
        }

        return true
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun registerUser(name: String, email: String, password: String) {
        showLoading(true)

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()

                    user?.updateProfile(profileUpdates)?.addOnCompleteListener { profileTask ->
                        showLoading(false)

                        if (profileTask.isSuccessful) {
                            Toast.makeText(requireContext(), "¡Cuenta creada exitosamente!", Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.mainFlow)
                        } else {
                            Toast.makeText(requireContext(), "Error al actualizar perfil", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    showLoading(false)
                    Toast.makeText(requireContext(), "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnRegister.isEnabled = !show
        binding.btnGoToLogin.isEnabled = !show
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}