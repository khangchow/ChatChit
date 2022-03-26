package com.dhk.chatchit.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chow.chinesedicev2.local.AppPrefs
import com.dhk.chatchit.model.Message
import com.chow.chinesedicev2.model.User
import com.dhk.chatchit.model.JoinRoomModel
import com.dhk.chatchit.model.UserState
import com.dhk.chatchit.model.convertToMessageNotification
import com.dhk.chatchit.utils.Constants
import com.google.gson.Gson
import io.socket.client.Socket
import org.json.JSONObject

class ChatViewModel(private val mSocket: Socket, private val appPrefs: AppPrefs): ViewModel() {
    private val _chat = MutableLiveData<Message>()
    val chat: LiveData<Message> get() =  _chat

    lateinit var user: User
    lateinit var room: String

    fun joinRoom(room: String) {
        this.room = room

        user = Gson().fromJson(appPrefs.getString(Constants.KEY_USER_DATA), User::class.java)

        mSocket.emit("newUserJoinedRoom", Gson().toJson(JoinRoomModel(user.username, room)))
    }

    fun leaveRoom() {
        mSocket.emit("leftRoom", user.username)
    }

    fun listenUserState() {
        mSocket.on("userState") {
            _chat.postValue(Gson().fromJson(it[0].toString(), UserState::class.java).convertToMessageNotification())
            Log.d("KHANG", "listenUserState: "+Gson().fromJson(it[0].toString(), UserState::class.java))
            Log.d("KHANG", "listenUserState: "+Gson().fromJson(it[0].toString(), UserState::class.java).convertToMessageNotification())
        }
    }

    fun sendMessage(msg: String) {
        Log.d("KHANG", "sendMessage: "+Message(user.id, Constants.TYPE_MESSAGE, user.username, msg, room))
        mSocket.emit("sendMessage", Gson().toJson(Message(user.id, Constants.TYPE_MESSAGE, user.username, msg, room)))
    }

    fun listenChatEvent() {
        mSocket.on("newMessage") {
            Log.d("KHANG", "listenChatEvent: ${it[0]}")
            _chat.postValue(Gson().fromJson(it[0].toString(), Message::class.java))
        }
    }
}