package com.dhk.chatchit.ui.lobby

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhk.chatchit.local.AppPrefs
import com.dhk.chatchit.base.BaseResponse
import com.dhk.chatchit.model.RoomStatusModel
import com.dhk.chatchit.model.UserResponse
import com.dhk.chatchit.model.toRoomStatusModelList
import com.dhk.chatchit.model.toUserModel
import com.dhk.chatchit.utils.Constants
import com.dhk.chatchit.utils.Constants.EVENT_JOINED_LOBBY
import com.dhk.chatchit.utils.Constants.EVENT_LEFT_ROOM
import com.google.gson.Gson
import io.socket.client.Socket
import kotlinx.coroutines.launch

class LobbyViewModel(private val mSocket: Socket, private val lobbyRepo: LobbyRepo, private val appPrefs: AppPrefs): ViewModel() {
    private val _action = MutableLiveData<LobbyAction>()
    val action: LiveData<LobbyAction> get() = _action

    lateinit var room: String

    fun joinLobby(username: String) {
        mSocket.connect()
        mSocket.emit(EVENT_JOINED_LOBBY, username)
        mSocket.on(EVENT_JOINED_LOBBY) {
            val user = Gson().fromJson(it[0].toString(), UserResponse::class.java).toUserModel()
            appPrefs.putString(Constants.KEY_USER_DATA, Gson().toJson(user))
            _action.postValue(LobbyAction.JoinedLobby(username))
        }
        mSocket.on(EVENT_LEFT_ROOM) {
            _action.postValue(LobbyAction.LeftRoom)
        }
    }

    fun outLobby() {
        mSocket.disconnect()
    }

    fun getRooms() {
        viewModelScope.launch {
            when(val result = lobbyRepo.getRooms()) {
                is BaseResponse.Success -> result.response.let {
                    if (it.error.isEmpty()) _action.postValue(LobbyAction.GetRooms(it.data.toRoomStatusModelList()))
                }
                is BaseResponse.Error -> Log.d("ERROR", result.exception.message.toString())
                else -> Unit
            }
        }
    }

    fun newRoom(name: String) {
        room = name
        viewModelScope.launch {
            when(val result = lobbyRepo.newRoom(name)) {
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
            when (val result = lobbyRepo.checkRoom(name)) {
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
    class GetRooms(val rooms: List<RoomStatusModel>) : LobbyAction()
    object NewRoomCreated : LobbyAction()
    class ValidRoomToJoin(val roomName: String) : LobbyAction()
    object ErrorRepeatedRoomName: LobbyAction()
    object ErrorInvalidRoom: LobbyAction()
    object LeftRoom : LobbyAction()
}