package com.dhk.chatchit.base

import androidx.lifecycle.ViewModel
import com.dhk.chatchit.local.AppPrefs
import com.dhk.chatchit.model.UserModel
import com.dhk.chatchit.other.Constants
import com.google.gson.Gson
import io.socket.client.Socket

open class BaseViewModel(
    private val appPrefs: AppPrefs,
    private val mSocket: Socket,
) : ViewModel() {
    protected val user: UserModel by lazy { Gson().fromJson(appPrefs.getString(Constants.KEY_USER_DATA), UserModel::class.java) }

    fun leaveRoom() {
        mSocket.emit(Constants.EVENT_LEFT_ROOM, user.username)
    }

    fun outLobby() {
        mSocket.disconnect()
    }
}