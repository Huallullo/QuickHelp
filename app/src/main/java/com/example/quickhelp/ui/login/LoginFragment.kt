package com.example.quickhelp.ui.login

import android.content.Intent
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
import com.example.quickhelp.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.GoogleAuthProvider
import com.example.quickhelp.R

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()

        // Configuración de Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        setupClickListeners()
        setupPasswordToggle()

        return binding.root
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateInputs(email, password)) {
                loginUser(email, password)
            }
        }

        binding.btnGoogle.setOnClickListener {
            signInWithGoogle()
        }

        binding.btnGoToRegister.setOnClickListener {
            navigateToRegister()
        }
    }

    private fun setupPasswordToggle() {
        binding.textInputLayoutPassword.setEndIconOnClickListener {
            val passwordEditText = binding.etPassword
            if (passwordEditText.transformationMethod is PasswordTransformationMethod) {
                // Mostrar contraseña
                passwordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.textInputLayoutPassword.endIconDrawable =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_visibility)
            } else {
                // Ocultar contraseña
                passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.textInputLayoutPassword.endIconDrawable =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_visibility_off)
            }
            // Mantener cursor al final
            passwordEditText.setSelection(passwordEditText.text?.length ?: 0)
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            binding.etEmail.error = "El correo electrónico es requerido"
            binding.etEmail.requestFocus()
            return false
        }

        if (password.isEmpty()) {
            binding.etPassword.error = "La contraseña es requerida"
            binding.etPassword.requestFocus()
            return false
        }

        if (password.length < 6) {
            binding.etPassword.error = "La contraseña debe tener al menos 6 caracteres"
            binding.etPassword.requestFocus()
            return false
        }

        return true
    }

    private fun loginUser(email: String, password: String) {
        showLoading(true)

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                showLoading(false)

                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                    navigateToMainApp()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Error: ${task.exception?.message ?: "Error desconocido"}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun signInWithGoogle() {
        showLoading(true)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST_CODE)
    }

    private fun navigateToRegister() {
        findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
    }

    private fun navigateToMainApp() {
        findNavController().navigate(R.id.action_loginFragment_to_mainFlow)
    }

    private fun showLoading(show: Boolean) {
        if (show) {
            binding.progressBar.visibility = View.VISIBLE
            binding.btnLogin.isEnabled = false
            binding.btnGoogle.isEnabled = false
            binding.btnGoToRegister.isEnabled = false
        } else {
            binding.progressBar.visibility = View.GONE
            binding.btnLogin.isEnabled = true
            binding.btnGoogle.isEnabled = true
            binding.btnGoToRegister.isEnabled = true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_SIGN_IN_REQUEST_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.result
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                auth.signInWithCredential(credential)
                    .addOnCompleteListener { authTask ->
                        showLoading(false)

                        if (authTask.isSuccessful) {
                            Toast.makeText(requireContext(), "Inicio de sesión con Google exitoso", Toast.LENGTH_SHORT).show()
                            navigateToMainApp()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Error al autenticar con Google: ${authTask.exception?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            } catch (e: Exception) {
                showLoading(false)
                Toast.makeText(
                    requireContext(),
                    "Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val GOOGLE_SIGN_IN_REQUEST_CODE = 100
    }
}