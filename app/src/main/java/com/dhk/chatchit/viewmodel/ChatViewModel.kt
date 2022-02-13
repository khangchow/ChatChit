package com.dhk.chatchit.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chow.chinesedicev2.local.AppPrefs
import com.dhk.chatchit.model.Message
import com.chow.chinesedicev2.model.User
import com.dhk.chatchit.utils.Constants
import com.google.gson.Gson
import io.socket.client.Socket
import org.json.JSONObject

class ChatViewModel(private val mSocket: Socket, private val appPrefs: AppPrefs): ViewModel() {
    private val _username = MutableLiveData<String>()
    val username: LiveData<String> get() =  _username

    private val _chat = MutableLiveData<Message>()
    val chat: LiveData<Message> get() =  _chat

    private var first = true
    var user: User?= null

    fun joinChatRoom(username: String) {
        mSocket.connect()
        mSocket.emit("newUser", username)
    }

    fun outChatRoom() {
        mSocket.disconnect()
    }

    fun listenPlayerJoin() {
        mSocket.on("newUser") {
            val json = JSONObject(it[0].toString())

            if (first) {
                user = Gson().fromJson(it[0].toString(), User::class.java)

                appPrefs.putString(Constants.KEY_USER_DATA, Gson().toJson(user))

                first = false
            }

            if (user!!.id != json.getString("id"))
                _username.postValue(json.getString("username"))
        }
    }

    fun sendMessage(msg: String) {
        mSocket.emit("chatMessage", Gson().toJson(Message(user!!.id, user!!.username, msg)))
    }

    fun listenChatEvent() {
        mSocket.on("chatMessage") {
            _chat.postValue(Gson().fromJson(it[0].toString(), Message::class.java))
        }
    }
}