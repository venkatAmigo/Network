package com.example.network

import android.app.Application

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Prefs = getSharedPreferences("USER", MODE_PRIVATE)
    }
}