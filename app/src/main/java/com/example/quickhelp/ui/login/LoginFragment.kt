package com.example.quickhelp.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

        return binding.root
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                loginUser(email, password)
            }
        }

        binding.btnGoogle.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, 100)
        }

        binding.btnGoToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun loginUser(email: String, password: String) {
        // Si no tienes progressBar en tu layout, elimina estas líneas
        // binding.progressBar.visibility = View.VISIBLE
        binding.btnLogin.isEnabled = false

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                // Si no tienes progressBar en tu layout, elimina estas líneas
                // binding.progressBar.visibility = View.GONE
                binding.btnLogin.isEnabled = true

                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                    // Navegar al flujo principal
                    findNavController().navigate(R.id.action_loginFragment_to_mainFlow)
                } else {
                    Toast.makeText(requireContext(), "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.result
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                // Si no tienes progressBar en tu layout, elimina estas líneas
                // binding.progressBar.visibility = View.VISIBLE
                binding.btnGoogle.isEnabled = false

                auth.signInWithCredential(credential)
                    .addOnCompleteListener { authTask ->
                        // Si no tienes progressBar en tu layout, elimina estas líneas
                        // binding.progressBar.visibility = View.GONE
                        binding.btnGoogle.isEnabled = true

                        if (authTask.isSuccessful) {
                            Toast.makeText(requireContext(), "Inicio de sesión con Google exitoso", Toast.LENGTH_SHORT).show()
                            // Navegar al flujo principal
                            findNavController().navigate(R.id.action_loginFragment_to_mainFlow)
                        } else {
                            Toast.makeText(requireContext(), "Error al autenticar con Google", Toast.LENGTH_SHORT).show()
                        }
                    }
            } catch (e: Exception) {
                // Si no tienes progressBar en tu layout, elimina estas líneas
                // binding.progressBar.visibility = View.GONE
                binding.btnGoogle.isEnabled = true
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}