package com.dhk.chatchit.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chow.chinesedicev2.local.AppPrefs
import com.chow.chinesedicev2.model.User
import com.dhk.chatchit.model.RoomStatus
import com.dhk.chatchit.repository.RoomRepo
import com.dhk.chatchit.base.BaseResponse
import com.dhk.chatchit.model.BaseResponseModel
import com.dhk.chatchit.model.Message
import com.dhk.chatchit.utils.Constants
import com.google.gson.Gson
import io.socket.client.Socket
import kotlinx.coroutines.launch

class LobbyViewModel(private val mSocket: Socket, private val roomRepo: RoomRepo, private val appPrefs: AppPrefs): ViewModel() {
    private val _rooms = MutableLiveData<BaseResponseModel<List<RoomStatus>>>()
    val rooms: LiveData<BaseResponseModel<List<RoomStatus>>> get() = _rooms

    private val _message = MutableLiveData<BaseResponseModel<String>>()
    val message: LiveData<BaseResponseModel<String>> get() = _message

    private val _check = MutableLiveData<BaseResponseModel<String>>()
    val check: LiveData<BaseResponseModel<String>> get() = _check

    lateinit var room: String

    fun joinLobby(username: String) {
        mSocket.connect()

        mSocket.emit("newUser", username)

        mSocket.on("self") {
            val user = Gson().fromJson(it[0].toString(), User::class.java)

            appPrefs.putString(Constants.KEY_USER_DATA, Gson().toJson(user))

            _message.postValue(BaseResponseModel("Welcome ${user.username}!", ""))
        }

        mSocket.on("leftRoom") {
            _message.postValue(BaseResponseModel(it[0].toString(), ""))
        }
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

    fun newRoom(name: String) {
        room = name

        viewModelScope.launch {
            when(val result = roomRepo.newRoom(name)) {
                is BaseResponse.Success -> result.response.let { _message.postValue(it) }
                is BaseResponse.Error -> Log.d("ERROR", result.exception.message.toString())
                else -> Unit
            }
        }
    }

    fun checkRoom(name: String) {
        viewModelScope.launch {
            when(val result = roomRepo.checkRoom(name)) {
                is BaseResponse.Success -> result.response.let { _check.postValue(it) }
                is BaseResponse.Error -> Log.d("ERROR", result.exception.message.toString())
                else -> Unit
            }
        }
    }
}