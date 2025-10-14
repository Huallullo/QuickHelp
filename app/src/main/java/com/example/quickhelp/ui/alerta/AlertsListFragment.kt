package com.example.quickhelp.ui.alerta

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quickhelp.databinding.FragmentAlertsListBinding
import com.example.quickhelp.ui.alerta.adapter.AlertsAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.android.material.snackbar.Snackbar

class AlertsListFragment : Fragment() {

    private var _binding: FragmentAlertsListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: AlertsAdapter
    private val db = FirebaseFirestore.getInstance()
    private var listenerRegistration: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlertsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = AlertsAdapter { alert ->
            Snackbar.make(requireView(), "Alerta: ${alert.type}", Snackbar.LENGTH_SHORT).show()
        }

        binding.rvAlerts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAlerts.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener {
            attachListener() // recargar
        }

        attachListener()
    }

    private fun attachListener() {
        binding.swipeRefresh.isRefreshing = true

        // Eliminar listener previo (si existe)
        listenerRegistration?.remove()

        listenerRegistration = db.collection("alerts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                binding.swipeRefresh.isRefreshing = false

                if (error != null) {
                    Snackbar.make(requireView(), "Error: ${error.message}", Snackbar.LENGTH_LONG).show()
                    return@addSnapshotListener
                }

                if (snapshot == null || snapshot.isEmpty) {
                    adapter.submitList(emptyList())
                    binding.tvEmpty.visibility = View.VISIBLE
                    return@addSnapshotListener
                }

                binding.tvEmpty.visibility = View.GONE

                val alerts = snapshot.documents.mapNotNull { doc ->
                    try {
                        Alert(
                            id = doc.getString("id") ?: doc.id,
                            userId = doc.getString("userId"),
                            type = doc.getString("type") ?: "",
                            description = doc.getString("description") ?: "",
                            latitude = doc.getDouble("latitude") ?: 0.0,
                            longitude = doc.getDouble("longitude") ?: 0.0,
                            timestamp = normalizeTimestamp(doc.get("timestamp")),
                            status = doc.getString("status") ?: "activa"
                        )
                    } catch (e: Exception) {
                        null
                    }
                }

                adapter.submitList(alerts)
            }
    }

    private fun normalizeTimestamp(value: Any?): Long {
        // Convierte cualquier tipo a milisegundos (Long)
        return when (value) {
            is Long -> if (value < 1_000_000_000_000L) value * 1000 else value
            is Double -> {
                val l = value.toLong()
                if (l < 1_000_000_000_000L) l * 1000 else l
            }
            else -> 0L
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        listenerRegistration?.remove()
        _binding = null
    }
}
