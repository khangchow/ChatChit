package com.dhk.chatchit.ui.chat_room

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chow.chinesedicev2.utils.KeyboardUtils
import com.dhk.chatchit.R
import com.dhk.chatchit.base.BaseActivity
import com.dhk.chatchit.databinding.ActivityChatBinding
import com.dhk.chatchit.model.MessageModel
import com.dhk.chatchit.utils.Constants
import com.dhk.chatchit.utils.showToast
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
                if (TextUtils.isEmpty(etMessage.text)) {
                    Toast.makeText(
                        this@ChatActivity,
                        getString(R.string.err_empty_msg),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    chatViewModel.sendMessage(etMessage.text.toString())
                    etMessage.clearFocus()
                    KeyboardUtils.hideKeyboard(etMessage)
                    etMessage.text = null
                }
            }
            tvScrollBot.setOnClickListener {
                rvChat.smoothScrollToPosition(chatList.size - 1)
                tvScrollBot.visibility = View.INVISIBLE
            }
            btnSelectIamge.setOnClickListener {

            }
        }
    }

    var setImageFromGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data
        val uri = data?.data
        chatViewModel.onImageSelected(uri)
//        val inputStream: InputStream? = uri?.let {
//            context?.getContentResolver()?.openInputStream(
//                it
//            )
//        }
//        val bitmap = BitmapFactory.decodeStream(inputStream)
//
//        //set selected image to imageview
//        binding.imgAvatar.setImageBitmap(bitmap)
    }

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
                    is RoomAction.OnSentMessage -> {
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