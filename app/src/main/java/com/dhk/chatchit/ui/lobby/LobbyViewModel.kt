package com.dhk.chatchit.ui.lobby

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
import com.dhk.chatchit.utils.Constants
import com.google.gson.Gson
import io.socket.client.Socket
import kotlinx.coroutines.launch

class LobbyViewModel(private val mSocket: Socket, private val roomRepo: RoomRepo, private val appPrefs: AppPrefs): ViewModel() {
    private val _action = MutableLiveData<LobbyAction>()
    val action: LiveData<LobbyAction> get() = _action

    lateinit var room: String

    fun joinLobby(username: String) {
        mSocket.connect()
        mSocket.emit("newUser", username)
        mSocket.on("self") {
            val user = Gson().fromJson(it[0].toString(), User::class.java)
            appPrefs.putString(Constants.KEY_USER_DATA, Gson().toJson(user))
            _action.postValue(LobbyAction.JoinedLobby(username))
        }
        mSocket.on("leftRoom") {
            _action.postValue(LobbyAction.LeftRoom)
        }
    }

    fun outLobby() {
        mSocket.disconnect()
    }

    fun getRooms() {
        viewModelScope.launch {
            when(val result = roomRepo.getRooms()) {
                is BaseResponse.Success -> result.response.let {
                    if (it.error.isEmpty()) _action.postValue(LobbyAction.GetRooms(it.data))
                }
                is BaseResponse.Error -> Log.d("ERROR", result.exception.message.toString())
                else -> Unit
            }
        }
    }

    fun newRoom(name: String) {
        room = name
        viewModelScope.launch {
            when(val result = roomRepo.newRoom(name)) {
                is BaseResponse.Success -> result.response.let {
                    if (it.error.isEmpty()) _action.postValue(LobbyAction.NewRoomCreated)
                    else _action.postValue(LobbyAction.ErrorRepeatedRoomName)
                }
                is BaseResponse.Error -> Log.d("ERROR", result.exception.message.toString())
                else -> Unit
            }
        }
    }

    fun checkRoom(name: String) {
        viewModelScope.launch {
            when (val result = roomRepo.checkRoom(name)) {
                is BaseResponse.Success -> result.response.let {
                    if (it.error.isEmpty()) _action.postValue(LobbyAction.ValidRoomToJoin(name))
                    else _action.postValue(LobbyAction.ErrorInvalidRoom)
                }
                is BaseResponse.Error -> Log.d("ERROR", result.exception.message.toString())
                else -> Unit
            }
        }
    }
}

sealed class LobbyAction {
    class JoinedLobby(val username: String) : LobbyAction()
    class GetRooms(val rooms: List<RoomStatus>) : LobbyAction()
    object NewRoomCreated : LobbyAction()
    class ValidRoomToJoin(val roomName: String) : LobbyAction()
    object ErrorRepeatedRoomName: LobbyAction()
    object ErrorInvalidRoom: LobbyAction()
    object LeftRoom : LobbyAction()
}