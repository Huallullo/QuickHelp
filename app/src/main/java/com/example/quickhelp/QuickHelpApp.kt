package com.example.quickhelp

import android.app.Application
import com.google.firebase.FirebaseApp

class QuickHelpApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Inicializa Firebase apenas se abra la app
        FirebaseApp.initializeApp(this)
    }
}
