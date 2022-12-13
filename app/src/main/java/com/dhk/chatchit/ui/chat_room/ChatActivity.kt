package com.dhk.chatchit.ui.chat_room

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
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
import com.dhk.chatchit.utils.Constants
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChatActivity : BaseActivity() {
    private lateinit var binding: ActivityChatBinding
    private val chatViewModel: ChatViewModel by viewModel()
    private var chatList = mutableListOf<MessageModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        val room = intent.getStringExtra(Constants.KEY_ROOM)
        chatViewModel.joinRoom(room!!)
        setContentView(binding.root)
        setUpViewModel()
        setUpChatRecycleView()
        binding.apply {
            tvRoomName.text = room
            btnBack.setOnClickListener {
                finish()
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
                private var alreadyShown = false
                private val EstimatedKeyboardDP = 148
                private val rect = Rect()
                override fun onGlobalLayout() {
                    root.getWindowVisibleDisplayFrame(rect)
                    val isShown = root.rootView.height - (rect.bottom - rect.top) >=
                            TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP, EstimatedKeyboardDP.toFloat(),
                                root.resources.displayMetrics
                            ).toInt()
                    if (isShown == alreadyShown) return
                    alreadyShown = isShown
                    if (isShown) rvChat.apply {
                        scrollToPosition((adapter as ChatAdapter).itemCount - 1)
                    }
                }
            })
        }
    }

    private val getImageFromGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        result.data?.data?.let {
            chatViewModel.onImageSelected(it)
        } ?: kotlin.run {
            showToast(getString(R.string.load_image_error))
        }
    }


    //
    //        //set selected image to imageview
    //        binding.imgAvatar.setImageBitmap(bitmap)

    private fun setUpChatRecycleView() {
        binding.apply {
            rvChat.adapter = ChatAdapter(mutableListOf())
            rvChat.layoutManager = LinearLayoutManager(this@ChatActivity)
            rvChat.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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

    private fun setUpViewModel() {
        binding.apply {
            chatViewModel.action.observe(this@ChatActivity) {
                when (it) {
                    is RoomAction.OnReceivedNewMessage -> {
                        chatList.add(it.mes)
                        (rvChat.adapter as ChatAdapter).addNewMessage(it.mes)
                        if (rvChat.canScrollVertically(1)) {
                            tvScrollBot.visibility = View.VISIBLE
                        } else {
                            rvChat.smoothScrollToPosition(chatList.size - 1)

                        }
                    }
                    is RoomAction.OnSendingMessage -> {
                        (rvChat.adapter as ChatAdapter).addNewMessage(it.mes)
                        chatList.add(it.mes)
                        rvChat.smoothScrollToPosition(chatList.size - 1)
                    }
                    is RoomAction.OnSentMessageSuccessfully -> {
                        (rvChat.adapter as ChatAdapter).updateMessageStatus(it.mes)
                    }
                    is RoomAction.ShowToastLoadingImageError -> {
                        showToast(getString(R.string.load_image_error))
                    }
                    is RoomAction.OnLoadedImageSuccessfully -> {

                    }
                    else -> {}
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

    override fun onDestroy() {
        chatViewModel.leaveRoom()
        super.onDestroy()
    }
}