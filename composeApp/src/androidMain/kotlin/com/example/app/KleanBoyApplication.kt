package com.example.app

import android.app.Application
import org.koin.android.ext.koin.androidContext

class KleanBoyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@KleanBoyApplication)
        }
    }
}