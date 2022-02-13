package com.dhk.chatchit.di

import android.content.Context
import android.content.SharedPreferences
import com.chow.chinesedicev2.local.AppPrefs
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import io.socket.client.Socket
import io.socket.client.IO


val module = module {
    single { connectSocket() }

    single { provideSharePreferences(androidApplication() as App)}
    single { AppPrefs(get()) }
}

fun provideSharePreferences(app: App): SharedPreferences {
    return app.applicationContext.getSharedPreferences(
        app.applicationContext.packageName,
        Context.MODE_PRIVATE
    )
}

fun connectSocket(): Socket = IO.socket("https://d338-171-250-188-147.ngrok.io")
