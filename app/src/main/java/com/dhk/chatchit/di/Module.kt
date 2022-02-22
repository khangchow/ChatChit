package com.dhk.chatchit.di

import android.content.Context
import android.content.SharedPreferences
import com.chow.chinesedicev2.local.AppPrefs
import com.dhk.chatchit.api.Api
import com.dhk.chatchit.repository.RoomRepo
import com.dhk.chatchit.viewmodel.ChatViewModel
import com.dhk.chatchit.viewmodel.LobbyViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import io.socket.client.Socket
import io.socket.client.IO
import okhttp3.OkHttpClient
import org.koin.androidx.viewmodel.dsl.viewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


val module = module {
    single { provideSharePreferences(androidApplication() as App)}
    single { AppPrefs(get()) }

    single { connectSocket() }

    single { provideRetrofit() }
    single { provideApiService(get()) }

    single { RoomRepo(get()) }

    viewModel { ChatViewModel(get(), get()) }
    viewModel { LobbyViewModel(get(), get()) }
}

fun provideSharePreferences(app: App): SharedPreferences {
    return app.applicationContext.getSharedPreferences(
        app.applicationContext.packageName,
        Context.MODE_PRIVATE
    )
}

fun connectSocket(): Socket = IO.socket("https://0d75-115-75-223-191.ngrok.io")

fun provideRetrofit(): Retrofit {
    return Retrofit.Builder()
        .baseUrl("https://0d75-115-75-223-191.ngrok.io")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

fun provideApiService(retrofit: Retrofit): Api {
    return retrofit.create(Api::class.java)
}


