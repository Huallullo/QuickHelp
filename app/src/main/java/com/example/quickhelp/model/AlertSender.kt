package com.example.quickhelp.model

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.*

object AlertSender {

    suspend fun sendAlert(
        type: String,
        description: String,
        latitude: Double,
        longitude: Double
    ) {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "anon"
        val alertId = UUID.randomUUID().toString()

        val alert = hashMapOf(
            "id" to alertId,
            "userId" to userId,
            "type" to type,
            "description" to description,
            "latitude" to latitude,
            "longitude" to longitude,
            "timestamp" to System.currentTimeMillis(),
            "status" to "activa"
        )

        try {
            db.collection("alerts").document(alertId).set(alert).await()
            Log.d("ALERT", "✅ Alerta enviada correctamente")
        } catch (e: Exception) {
            Log.e("ALERT", "❌ Error al enviar alerta: ${e.message}")
            throw e
        }
    }
}
