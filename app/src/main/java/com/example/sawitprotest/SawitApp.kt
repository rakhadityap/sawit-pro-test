package com.example.sawitprotest

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.database.database
import com.google.firebase.initialize

class SawitApp: Application() {
    override fun onCreate() {
        super.onCreate()

        Firebase.initialize(this)
        Firebase.database.setPersistenceEnabled(true)

//        FirebaseApp.initializeApp(this)
    }
}