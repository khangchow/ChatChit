package com.dhk.chatchit.ui.chat_room

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dhk.chatchit.R
import com.dhk.chatchit.databinding.ActivityChatBinding
import com.dhk.chatchit.extension.hide
import com.dhk.chatchit.extension.show
import com.dhk.chatchit.extension.showToast
import com.dhk.chatchit.extension.toSizeInDp
import com.dhk.chatchit.model.LoadingMode
import com.dhk.chatchit.other.Constants
import com.dhk.chatchit.other.PermissionUtils
import com.dhk.chatchit.other.Resource
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private val chatViewModel: ChatViewModel by viewModel()
    private val roomName by lazy { intent.getStringExtra(Constants.KEY_ROOM) ?: "" }
    private var isKeyboardShown = false
    private var isScrolling = false
    private var isPaddingShown = false
    private var isLoadedAllMessages = false
    private var isBottom = false
    private val getImageFromGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        result.data?.data?.let {
            chatViewModel.onImageSelected(it)
        } ?: kotlin.run {
            showToast(getString(R.string.load_image_error))
        }
    }
    private val requestStoragePermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            if (it[PermissionUtils.READ_EXTERNAL_STORAGE] == true && it[PermissionUtils.WRITE_EXTERNAL_STORAGE] == true) {
                getImageFromGallery.launch(Intent(Intent.ACTION_PICK).apply { type = "image/*" })
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
                rvChat.apply {
                    smoothScrollToPosition((adapter as ChatAdapter).messages.size - 1)
                }
                tvScrollBot.visibility = View.INVISIBLE
            }
            btnSelectImage.setOnClickListener {
                if (PermissionUtils.isStoragePermissionsGranted(this@ChatActivity)) {
                    getImageFromGallery.launch(Intent(Intent.ACTION_PICK).apply { type = "image/*" })
                } else {
                    requestStoragePermissionLauncher.launch(arrayOf(PermissionUtils.READ_EXTERNAL_STORAGE, PermissionUtils.WRITE_EXTERNAL_STORAGE))
                }
            }
            root.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
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
                    if (isShown && isBottom) rvChat.apply {
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
                        when (newState) {
                            RecyclerView.SCROLL_STATE_DRAGGING -> isScrolling = true
                            RecyclerView.SCROLL_STATE_IDLE -> isScrolling = false
                        }
                        if (!recyclerView.canScrollVertically(1)) {
                            if (tvScrollBot.visibility == View.VISIBLE) {
                                tvScrollBot.visibility = View.INVISIBLE
                            }
                            isBottom = true
                        } else {
                            isBottom = false
                        }
                    }

                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        if (isScrolling && recyclerView.canScrollVertically(-1).not() && dy <= 0) {
                            if (isPaddingShown.not()) {
                                setPadding(0, (50).toSizeInDp(), 0, 0)
                                isPaddingShown = true
                            }
                            if (isLoadedAllMessages.not()) {
                                pbLoading.show()
                                chatViewModel.loadChatHistory(LoadingMode.LOAD_MORE)
                            }
                        }
                    }
                })
            }
        }
    }

    private fun setUpViewModel() {
        binding.apply {
            chatViewModel.loadMoreChatHistory.observe(this@ChatActivity) { event ->
                if (event.hasBeenHandled.not()) {
                    event.getContentIfNotHandled()?.let { resource ->
                        when (resource) {
                            is Resource.Success -> {
                                resource.data?.let {
                                    (rvChat.adapter as ChatAdapter).apply {
                                        messages.addAll(0, it)
                                        notifyItemRangeInserted(0, it.size)
                                    }
                                } ?: kotlin.run {
                                    rvChat.setPadding(0, 0, 0, 0)
                                    isLoadedAllMessages = true
                                }
                            }
                            else -> Unit
                        }
                        pbLoading.hide()
                    }
                }
            }
            chatViewModel.loadChatHistory.observe(this@ChatActivity) { event ->
                if (event.hasBeenHandled.not()) {
                    event.getContentIfNotHandled()?.let { resource ->
                        when (resource) {
                            is Resource.Success -> {
                                resource.data?.let {
                                    rvChat.apply rvChat@{
                                        (adapter as ChatAdapter).apply {
                                            messages.addAll(it)
                                            notifyItemRangeInserted(0, it.size)
                                        }.also {
                                            scrollToPosition(it.itemCount - 1)
                                        }
                                    }
                                    pbLoading.hide()
                                }
                            }
                            else -> Unit
                        }
                    }
                }
            }
            chatViewModel.networkCallStatus.observe(this@ChatActivity) { event ->
                if (event.hasBeenHandled.not()) {
                    event.getContentIfNotHandled()?.let { resource ->
                        when (resource) {
                            is Resource.Error -> {
                                showToastError()
                            }
                            else -> Unit
                        }
                    }
                }
            }
            chatViewModel.newMessage.observe(this@ChatActivity) { event ->
                if (event.hasBeenHandled.not()) {
                    event.getContentIfNotHandled()?.let { message ->
                        rvChat.apply {
                            (adapter as ChatAdapter).apply {
                                addNewMessage(message)
                            }.also {
                                if (canScrollVertically(1)) {
                                    tvScrollBot.visibility = View.VISIBLE
                                } else {
                                    smoothScrollToPosition((adapter as ChatAdapter).messages.size - 1)
                                }
                            }
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
                        rvChat.apply {
                            (adapter as ChatAdapter).apply {
                                addNewMessage(message)
                            }.also {
                                scrollToPosition(it.messages.size - 1)
                            }
                        }
                    }
                }
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

    override fun onDestroy() {
        chatViewModel.leaveRoom()
        super.onDestroy()
    }

    private fun showToastError() {
        showToast(getString(R.string.common_error))
    }
}