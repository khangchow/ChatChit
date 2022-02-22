package com.dhk.chatchit.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhk.chatchit.model.RoomStatus
import com.dhk.chatchit.repository.RoomRepo
import com.dhk.chatchit.base.BaseResponse
import com.dhk.chatchit.model.BaseResponseModel
import io.socket.client.Socket
import kotlinx.coroutines.launch

class LobbyViewModel(private val mSocket: Socket, private val roomRepo: RoomRepo): ViewModel() {
    private val _rooms = MutableLiveData<BaseResponseModel<List<RoomStatus>>>()
    val rooms: LiveData<BaseResponseModel<List<RoomStatus>>> get() = _rooms

    fun joinLobby(username: String) {
        mSocket.connect()
        mSocket.emit("newUser", username)
    }

    fun outLobby() {
        mSocket.disconnect()
    }

    fun getRooms() {
        viewModelScope.launch {
            when(val result = roomRepo.getRooms()) {
                is BaseResponse.Success -> result.response.let { _rooms.postValue(it) }
                is BaseResponse.Error -> Log.d("ERROR", result.exception.message.toString())
                else -> Unit
            }
        }
    }
}