package com.calibraint.notes_rt

import android.app.Application
import com.google.firebase.FirebaseApp


class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}