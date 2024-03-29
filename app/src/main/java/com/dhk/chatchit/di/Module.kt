package com.dhk.chatchit.di

import android.content.Context
import android.content.SharedPreferences
import com.dhk.chatchit.local.AppPrefs
import com.dhk.chatchit.api.Api
import com.dhk.chatchit.ui.chat_room.ChatRepo
import com.dhk.chatchit.ui.lobby.LobbyRepo
import com.dhk.chatchit.other.Constants
import com.dhk.chatchit.ui.chat_room.ChatViewModel
import com.dhk.chatchit.ui.lobby.LobbyViewModel
import com.dhk.chatchit.ui.login.LoginViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import io.socket.client.Socket
import io.socket.client.IO
import org.koin.androidx.viewmodel.dsl.viewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


val module = module {
    single { provideSharePreferences(androidApplication() as App)}
    single { AppPrefs(get()) }

    single { connectSocket() }

    single { provideRetrofit() }
    single { provideApiService(get()) }

    single { LobbyRepo(get()) }
    single { ChatRepo(get()) }

    viewModel { ChatViewModel(get(), get(), get()) }
    viewModel { LobbyViewModel(get(), get(), get()) }
    viewModel { LoginViewModel() }
}

fun provideSharePreferences(app: App): SharedPreferences {
    return app.applicationContext.getSharedPreferences(
        app.applicationContext.packageName,
        Context.MODE_PRIVATE
    )
}

fun connectSocket(): Socket = IO.socket(Constants.BASE_URL)

fun provideRetrofit(): Retrofit {
    return Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

fun provideApiService(retrofit: Retrofit): Api {
    return retrofit.create(Api::class.java)
}


