package com.dhk.chatchit.ui.lobby

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dhk.chatchit.base.BaseViewModel
import com.dhk.chatchit.local.AppPrefs
import com.dhk.chatchit.model.RoomStatusModel
import com.dhk.chatchit.model.UserResponse
import com.dhk.chatchit.model.toRoomStatusModelList
import com.dhk.chatchit.model.toUserModel
import com.dhk.chatchit.other.Constants
import com.dhk.chatchit.other.Constants.EVENT_JOINED_LOBBY
import com.dhk.chatchit.other.Event
import com.dhk.chatchit.other.Resource
import com.dhk.chatchit.other.Validator
import com.google.gson.Gson
import io.socket.client.Socket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LobbyViewModel(
    private val mSocket: Socket,
    private val lobbyRepo: LobbyRepo,
    private val appPrefs: AppPrefs
) : BaseViewModel(appPrefs, mSocket) {
    private val _joinLobbyStatus = MutableLiveData<Event<String>>()
    val joinLobbyStatus = _joinLobbyStatus
    private val _rooms = MutableLiveData<Resource<List<RoomStatusModel>>>()
    val rooms = _rooms
    private val _createRoomStatus = MutableLiveData<Event<Resource<String>>>()
    val createRoomStatus = _createRoomStatus
    private val _checkRoomStatus = MutableLiveData<Event<Resource<String>>>()
    val checkRoomStatus = _checkRoomStatus
    private val _leaveRoomStatus = MutableLiveData<Unit>()
    val leaveRoomStatus = _leaveRoomStatus

    fun joinLobby(username: String) {
        _networkCallStatus.postValue(Event(Resource.Loading))
        mSocket.connect()
        mSocket.emit(EVENT_JOINED_LOBBY, username)
        mSocket.on(EVENT_JOINED_LOBBY) {
            val user = Gson().fromJson(it[0].toString(), UserResponse::class.java).toUserModel()
            appPrefs.putString(Constants.KEY_USER_DATA, Gson().toJson(user))
            _joinLobbyStatus.postValue(Event(username))
        }
        mSocket.on(Constants.EVENT_LEFT_ROOM) {
            _leaveRoomStatus.postValue(Unit)
        }
    }

    fun getRooms() {
        viewModelScope.launch(Dispatchers.IO) {
            _networkCallStatus.postValue(Event(Resource.Loading))
            try {
                with(lobbyRepo.getRooms()) {
                    if (isSuccessful && body() != null) {
                        _rooms.postValue(Resource.Success(body()!!.data.toRoomStatusModelList()))
                    } else _networkCallStatus.postValue(Event(Resource.Error()))
                }
            } catch (e: Exception) {
                _networkCallStatus.postValue(Event(Resource.Error()))
            }
        }
    }

    fun newRoom(name: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            _networkCallStatus.postValue(Event(Resource.Loading))
            if (!Validator.isRoomNameValid(name)) {
                _createRoomStatus.postValue(Event(Resource.Error()))
                return@launch
            }
            try {
                with(lobbyRepo.newRoom(name!!)) {
                    if (isSuccessful && body() != null) {
                        _createRoomStatus.postValue(
                            Event(
                                Resource.Success(
                                    name
                                )
                            )
                        )
                    } else _createRoomStatus.postValue(Event(Resource.Error()))
                }
            } catch (e: Exception) {
                _networkCallStatus.postValue(Event(Resource.Error()))
            }
        }
    }

    fun checkRoom(roomName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _networkCallStatus.postValue(Event(Resource.Loading))
            try {
                with(lobbyRepo.checkRoom(roomName)) {
                    if (isSuccessful && body() != null) {
                        _checkRoomStatus.postValue(
                            Event(
                                Resource.Success(
                                    roomName
                                )
                            )
                        )
                    } else _checkRoomStatus.postValue(Event(Resource.Error()))
                }
            } catch (e: Exception) {
                _networkCallStatus.postValue(Event(Resource.Error()))
            }
        }
    }
}