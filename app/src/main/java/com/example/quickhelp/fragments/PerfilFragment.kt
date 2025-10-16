package com.example.quickhelp.fragments

import android.content.Intent
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

class PerfilFragment : Fragment() {

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()

        setupUserInfo()
        setupClickListeners()

        return binding.root
    }

    private fun setupUserInfo() {
        val user = auth.currentUser
        user?.let {
            binding.tvUserName.text = it.displayName ?: "Usuario"
            binding.tvUserEmail.text = it.email ?: "No email"
        }
    }

    private fun setupClickListeners() {
        binding.btnLogout.setOnClickListener {
            logoutUser()
        }

        // Agrega otros listeners según necesites
    }

    private fun logoutUser() {
        auth.signOut()
        Toast.makeText(requireContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show()

        // Navegar al login fragment
        findNavController().navigate(R.id.loginFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}