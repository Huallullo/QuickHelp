package com.example.quickhelp.ui.alerta

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.quickhelp.databinding.FragmentAlertaBinding
import com.example.quickhelp.model.AlertSender
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class AlertaFragment : Fragment() {

    private var _binding: FragmentAlertaBinding? = null
    private val binding get() = _binding!!
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Lanzador de permisos
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (fineGranted || coarseGranted) {
            obtenerUbicacionYEnviar()
        } else {
            Snackbar.make(requireView(), "❌ Se requieren permisos de ubicación", Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlertaBinding.inflate(inflater, container, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        binding.btnEnviarAlerta.setOnClickListener {
            verificarPermisosYEnviar()
        }

        return binding.root
    }

    private fun verificarPermisosYEnviar() {
        val fineLocation = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseLocation = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)

        if (fineLocation == PackageManager.PERMISSION_GRANTED || coarseLocation == PackageManager.PERMISSION_GRANTED) {
            obtenerUbicacionYEnviar()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun obtenerUbicacionYEnviar() {
        // Usa getCurrentLocation para obtener coordenadas frescas
        fusedLocationClient.getCurrentLocation(
            com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
            null
        ).addOnSuccessListener { location ->
            if (location != null) {
                enviarAlerta(location.latitude, location.longitude)
            } else {
                Snackbar.make(requireView(), "⚠️ No se pudo obtener la ubicación actual", Snackbar.LENGTH_LONG).show()
            }
        }.addOnFailureListener { e ->
            Snackbar.make(requireView(), "❌ Error al obtener ubicación: ${e.message}", Snackbar.LENGTH_LONG).show()
        }
    }

    private fun enviarAlerta(lat: Double, lon: Double) {
        binding.btnEnviarAlerta.isEnabled = false

        lifecycleScope.launch {
            try {
                AlertSender.sendAlert(
                    type = "Emergencia médica",
                    description = "Persona inconsciente cerca",
                    latitude = lat,
                    longitude = lon
                )

                Snackbar.make(requireView(), "✅ Alerta enviada con ubicación actual", Snackbar.LENGTH_LONG).show()
            } catch (e: Exception) {
                Snackbar.make(requireView(), "❌ Error: ${e.message}", Snackbar.LENGTH_LONG).show()
            } finally {
                binding.btnEnviarAlerta.isEnabled = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
