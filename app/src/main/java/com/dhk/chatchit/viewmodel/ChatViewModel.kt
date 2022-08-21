package com.dhk.chatchit.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chow.chinesedicev2.local.AppPrefs
import com.chow.chinesedicev2.model.User
import com.dhk.chatchit.model.JoinRoomModel
import com.dhk.chatchit.model.Message
import com.dhk.chatchit.model.UserState
import com.dhk.chatchit.utils.Constants
import com.google.gson.Gson
import io.socket.client.Socket

class ChatViewModel(private val mSocket: Socket, private val appPrefs: AppPrefs) : ViewModel() {
    private val _action = MutableLiveData<RoomAction>()
    val action: LiveData<RoomAction> get() = _action
    lateinit var user: User
    lateinit var room: String

    fun joinRoom(room: String) {
        this.room = room
        user = Gson().fromJson(appPrefs.getString(Constants.KEY_USER_DATA), User::class.java)
        mSocket.emit("newUserJoinedRoom", Gson().toJson(JoinRoomModel(user.username, room)))
        mSocket.on("userState") {
            _action.postValue(
                RoomAction.NewMessage(
                    Gson().fromJson(
                        it[0].toString(),
                        UserState::class.java
                    ).convertToMessageNotification()
                )
            )
        }
        mSocket.on("newMessage") {
            _action.postValue(
                RoomAction.NewMessage(
                    Gson().fromJson(
                        it[0].toString(),
                        Message::class.java
                    )
                )
            )
        }
    }

    fun leaveRoom() {
        mSocket.emit("leftRoom", user.username)
    }

    fun sendMessage(msg: String) {
        mSocket.emit(
            "sendMessage",
            Gson().toJson(Message(user.id, Constants.TYPE_MESSAGE, user.username, msg, room))
        )
    }
}

sealed class RoomAction {
    class NewMessage(val mes: Message) : RoomAction()
}