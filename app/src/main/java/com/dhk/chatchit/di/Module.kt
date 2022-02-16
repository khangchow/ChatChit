package com.dhk.chatchit.di

import android.content.Context
import android.content.SharedPreferences
import com.chow.chinesedicev2.local.AppPrefs
import com.dhk.chatchit.viewmodel.ChatViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import io.socket.client.Socket
import io.socket.client.IO
import org.koin.androidx.viewmodel.dsl.viewModel


val module = module {
    single { connectSocket() }

    viewModel { ChatViewModel(get(), get()) }

    single { provideSharePreferences(androidApplication() as App)}
    single { AppPrefs(get()) }
}

fun provideSharePreferences(app: App): SharedPreferences {
    return app.applicationContext.getSharedPreferences(
        app.applicationContext.packageName,
        Context.MODE_PRIVATE
    )
}

fun connectSocket(): Socket = IO.socket("https://d164-171-250-188-147.ngrok.io")
