package com.example.quickhelp.ui.alerta.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.quickhelp.databinding.ItemAlertBinding
import com.example.quickhelp.ui.alerta.Alert
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class AlertsAdapter(
    private val onClick: (Alert) -> Unit
) : ListAdapter<Alert, AlertsAdapter.AlertViewHolder>(DiffCallback) {

    object DiffCallback : DiffUtil.ItemCallback<Alert>() {
        override fun areItemsTheSame(oldItem: Alert, newItem: Alert) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Alert, newItem: Alert) = oldItem == newItem
    }

    inner class AlertViewHolder(private val binding: ItemAlertBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(alert: Alert) {
            // 🔹 Tipo con ícono
            binding.tvType.text = "${getIconForType(alert.type)} ${alert.type}"

            // 🔹 Descripción, usuario y estado
            binding.tvDescription.text = alert.description
            binding.tvUser.text = "👤 ${alert.userId ?: "Desconocido"}"
            binding.tvStatus.text = "📍 Estado: ${alert.status}"
            binding.tvDate.text = formatTimestampHuman(alert.timestamp)

            // 🔹 Color dinámico según tipo
            val color = when {
                alert.type.contains("médica", true) || alert.type.contains("medica", true) -> 0xFFFFCDD2.toInt() // rojo suave
                alert.type.contains("incendio", true) -> 0xFFFFE0B2.toInt() // naranja
                alert.type.contains("robo", true) || alert.type.contains("asalto", true) -> 0xFFFFF59D.toInt() // amarillo
                alert.type.contains("accidente", true) -> 0xFFBBDEFB.toInt() // azul
                alert.type.contains("natural", true) || alert.type.contains("terremoto", true) -> 0xFFC8E6C9.toInt() // verde
                else -> 0xFFF5F5F5.toInt() // gris claro
            }

            // Aplica el color al fondo del card
            binding.root.setCardBackgroundColor(color)

            // Acción al pulsar el item
            binding.root.setOnClickListener { onClick(alert) }
        }


        private fun getIconForType(type: String): String {
            return when {
                type.contains("incendio", true) -> "🔥"
                type.contains("médica", true) || type.contains("medica", true) -> "🚨"
                type.contains("accidente", true) -> "💥"
                type.contains("robo", true) || type.contains("asalto", true) -> "⚠️"
                type.contains("natural", true) || type.contains("terremoto", true) -> "🌋"
                else -> "❗"
            }
        }

        private fun formatTimestampHuman(timestamp: Long): String {
            if (timestamp <= 0) return "Sin fecha"

            // 🔹 Asegurar milisegundos correctos
            val tsMillis = if (timestamp < 1_000_000_000_000L) timestamp * 1000 else timestamp

            val now = System.currentTimeMillis()
            val diff = now - tsMillis
            val date = Date(tsMillis)

            // 🔹 Zona horaria Lima (GMT-5)
            val limaZone = TimeZone.getTimeZone("America/Lima")
            val locale = Locale("es", "PE")

            // 🔹 Formatos 12h con AM/PM
            val sdfTime = SimpleDateFormat("hh:mm a", locale)
            val sdfDate = SimpleDateFormat("dd/MM/yyyy", locale)
            sdfTime.timeZone = limaZone
            sdfDate.timeZone = limaZone

            // 🔹 Capitalizar la primera letra
            fun String.cap() = this.replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }

            return when {
                diff < TimeUnit.MINUTES.toMillis(1) -> "hace un momento".cap()
                diff < TimeUnit.HOURS.toMillis(1) -> {
                    val mins = TimeUnit.MILLISECONDS.toMinutes(diff)
                    "hace $mins min (${sdfTime.format(date)})".cap()
                }
                diff < TimeUnit.DAYS.toMillis(1) -> {
                    val hours = TimeUnit.MILLISECONDS.toHours(diff)
                    "hace $hours h (${sdfTime.format(date)})".cap()
                }
                diff < TimeUnit.DAYS.toMillis(2) -> "ayer, ${sdfTime.format(date)}".cap()
                else -> "${sdfDate.format(date)}, ${sdfTime.format(date)}"
            }
        }



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val binding = ItemAlertBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlertViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
