package com.example.dao

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class SunnyWeatherApplication : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        const val TOKEN = "uH13sCMdhJgYrR5S"
    }
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}