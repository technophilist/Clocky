package com.example.clocky.di

import android.app.Application

class ClockyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
    
    fun getServiceContainer() = ServiceContainer()
}