package com.dhk.chatchit.di

import android.app.Application
import com.dhk.chatchit.other.Resources
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext
import org.koin.core.logger.Level

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        GlobalContext.startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@App)
            modules(module)
        }
        Resources.init(this)
    }
}