package com.example.drawingactivity

import android.app.Application
import com.example.drawingactivity.drawingdata.DrawingRepository
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class DrawingApplication : Application() {
    val scope = CoroutineScope(SupervisorJob())

    val DrawingtRepository by lazy { DrawingRepository(scope) }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}