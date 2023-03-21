package com.dhk.chatchit.ui.chat_room

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dhk.chatchit.R
import com.dhk.chatchit.base.BaseActivity
import com.dhk.chatchit.databinding.ActivityChatBinding
import com.dhk.chatchit.extension.showToast
import com.dhk.chatchit.model.MessageModel
import com.dhk.chatchit.other.Constants
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChatActivity : BaseActivity() {
    private lateinit var binding: ActivityChatBinding
    private val chatViewModel: ChatViewModel by viewModel()
    private var chatList = mutableListOf<MessageModel>()
    private val roomName by lazy { intent.getStringExtra(Constants.KEY_ROOM) ?: "" }
    private var isKeyboardShown = false
    private var alreadyLeftRoom = false
    private val getImageFromGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        result.data?.data?.let {
            chatViewModel.onImageSelected(it)
        } ?: kotlin.run {
            showToast(getString(R.string.load_image_error))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        chatViewModel.joinRoom(roomName)
        setContentView(binding.root)
        setUpViewModel()
        setUpChatRecycleView()
        setUpView()
    }

    private fun setUpView() {
        binding.apply {
            tvRoomName.text = roomName
            btnBack.setOnClickListener {
                chatViewModel.leaveRoom()
            }
            btnSend.setOnClickListener {
                etMessage.text.toString().let {
                    if (it.isNotBlank()) {
                        chatViewModel.sendMessage(it)
                        etMessage.text = null
                    }
                }
            }
            tvScrollBot.setOnClickListener {
                rvChat.smoothScrollToPosition(chatList.size - 1)
                tvScrollBot.visibility = View.INVISIBLE
            }
            btnSelectImage.setOnClickListener {
                getImageFromGallery.launch(Intent(Intent.ACTION_PICK).apply { type = "image/*" })
            }
            root.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                private val EstimatedKeyboardDP = 148
                private val rect = Rect()
                override fun onGlobalLayout() {
                    root.getWindowVisibleDisplayFrame(rect)
                    val isShown = root.rootView.height - (rect.bottom - rect.top) >=
                            TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP, EstimatedKeyboardDP.toFloat(),
                                root.resources.displayMetrics
                            ).toInt()
                    if (isShown == isKeyboardShown) return
                    isKeyboardShown = isShown
                    if (isShown) rvChat.apply {
                        scrollToPosition((adapter as ChatAdapter).itemCount - 1)
                    }
                }
            })
        }
    }

    private fun setUpChatRecycleView() {
        binding.apply {
            rvChat.run {
                adapter = ChatAdapter(mutableListOf())
                layoutManager = LinearLayoutManager(this@ChatActivity)
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)
                        if (!recyclerView.canScrollVertically(1) && recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                            if (tvScrollBot.visibility == View.VISIBLE) tvScrollBot.visibility =
                                View.INVISIBLE
                        }
                    }
                })
            }
        }
    }

    private fun setUpViewModel() {
        binding.apply {
            chatViewModel.newMessage.observe(this@ChatActivity) { event ->
                if (event.hasBeenHandled.not()) {
                    event.getContentIfNotHandled()?.let { message ->
                        chatList.add(message)
                        (rvChat.adapter as ChatAdapter).addNewMessage(message)
                        if (rvChat.canScrollVertically(1)) {
                            tvScrollBot.visibility = View.VISIBLE
                        } else {
                            rvChat.smoothScrollToPosition(chatList.size - 1)
                        }
                    }
                }
            }
            chatViewModel.sendMessageStatus.observe(this@ChatActivity) { event ->
                if (event.hasBeenHandled.not()) {
                    event.getContentIfNotHandled()?.let { message ->
                        (rvChat.adapter as ChatAdapter).updateMessageStatus(message)
                    }
                }
            }
            chatViewModel.sendTempMessageStatus.observe(this@ChatActivity) { event ->
                if (event.hasBeenHandled.not()) {
                    event.getContentIfNotHandled()?.let { message ->
                        (rvChat.adapter as ChatAdapter).addNewMessage(message)
                        chatList.add(message)
                        rvChat.smoothScrollToPosition(chatList.size - 1)
                    }
                }
            }
            chatViewModel.leaveRoomStatus.observe(this@ChatActivity) {
                alreadyLeftRoom = true
                finish()
            }
            chatViewModel.getImageFromDeviceErrorStatus.observe(this@ChatActivity) { event ->
                if (event.hasBeenHandled.not()) {
                    event.getContentIfNotHandled()?.let {
                        showToast(getString(R.string.load_image_error))
                    }
                }
            }
        }
    }

    companion object {
        fun getIntent(context: Context, room: String): Intent {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra(Constants.KEY_ROOM, room)
            return intent
        }
    }

    override fun onBackPressed() {
        if (isKeyboardShown.not()) chatViewModel.leaveRoom()
        else super.onBackPressed()
    }

    override fun onDestroy() {
        if (alreadyLeftRoom.not()) chatViewModel.leaveRoom()
        super.onDestroy()
    }
}