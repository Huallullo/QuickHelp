package com.example.quickhelp.ui.alerta

data class Alert(
    val id: String = "",
    val userId: String? = "",
    val type: String = "",
    val description: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timestamp: Long = 0,
    val status: String = ""

)