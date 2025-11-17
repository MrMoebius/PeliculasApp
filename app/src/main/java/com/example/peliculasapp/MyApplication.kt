package com.example.peliculasapp

import android.app.Application

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MovieManager.init(this)
    }
}
