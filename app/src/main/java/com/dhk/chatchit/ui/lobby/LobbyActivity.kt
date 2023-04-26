package com.dhk.chatchit.ui.lobby

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhk.chatchit.R
import com.dhk.chatchit.base.BaseActivity
import com.dhk.chatchit.databinding.ActivityLobbyBinding
import com.dhk.chatchit.dialog.CustomDialog
import com.dhk.chatchit.extension.*
import com.dhk.chatchit.other.Constants
import com.dhk.chatchit.other.Resource
import com.dhk.chatchit.ui.chat_room.ChatActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class LobbyActivity : BaseActivity() {
    private lateinit var binding: ActivityLobbyBinding
    private val lobbyViewModel: LobbyViewModel by viewModel()
    private var customDialog: CustomDialog? = null

    private fun getCreateRoomDialog(): CustomDialog {
        if (customDialog == null) customDialog = CustomDialog.getInstance().enableInputMode(true)
            .setTitle(getString(R.string.new_room))
            .setPositiveButton(getString(R.string.create)) {
                lobbyViewModel.newRoom(it)
            }
            .setNegativeButton(getString(R.string.cancel))
            .setNegativeButtonDismissOnClicked(true)
        return customDialog!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLobbyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lobbyViewModel.joinLobby(intent.getStringExtra(Constants.KEY_USERNAME).toString())
        setUpView()
        setUpViewModel()
    }

    override fun onResume() {
        super.onResume()
        lobbyViewModel.getRooms()
    }

    private fun setUpView() {
        binding.apply {
            swipeRefreshLayout.setOnRefreshListener {
                lobbyViewModel.getRooms()
            }
            rvRooms.adapter = RoomAdapter(onCLickedRoom = lobbyViewModel::checkRoom)
            rvRooms.layoutManager = LinearLayoutManager(this@LobbyActivity)
            btnNew.setOnClickListener {
                getCreateRoomDialog().show(supportFragmentManager, CustomDialog::class.java.simpleName)
            }
        }
    }

    private fun setUpViewModel() {
        binding.apply {
            lobbyViewModel.networkCallStatus.observe(this@LobbyActivity) { event ->
                if (event.hasBeenHandled.not()) {
                    event.getContentIfNotHandled()?.let { resource ->
                        when (resource) {
                            is Resource.Loading -> rlLoading.show()
                            is Resource.Error -> {
                                rlLoading.invisible()
                                showToastError()
                            }
                            else -> Unit
                        }
                    }
                }
            }
            lobbyViewModel.rooms.observe(this@LobbyActivity) { resource ->
                when (resource) {
                    is Resource.Success -> {
                        rlLoading.invisible()
                        resource.data?.let {
                            (rvRooms.adapter as RoomAdapter).setListObject(it)
                        }
                        swipeRefreshLayout.isRefreshing = false
                    }
                    else -> Unit
                }
            }
            lobbyViewModel.createRoomStatus.observe(this@LobbyActivity) { event ->
                if (event.hasBeenHandled.not()) {
                    event.getContentIfNotHandled()?.let { resource ->
                        rlLoading.invisible()
                        when (resource) {
                            is Resource.Success -> {
                                resource.data?.let { roomName ->
                                    customDialog?.dismiss()
                                    customDialog = null
                                    startActivity(ChatActivity.getIntent(this@LobbyActivity, roomName))
                                }
                            }
                            is Resource.Error -> {
                                showToast(getString(R.string.error_invalid_room_name_description))
                            }
                            else -> Unit
                        }
                    }
                }
            }
            lobbyViewModel.checkRoomStatus.observe(this@LobbyActivity) { event ->
                if (event.hasBeenHandled.not()) {
                    event.getContentIfNotHandled()?.let { resource ->
                        when (resource) {
                            is Resource.Success -> {
                                rlLoading.invisible()
                                resource.data?.let { roomName ->
                                    startActivity(ChatActivity.getIntent(this@LobbyActivity, roomName))
                                }
                            }
                            is Resource.Error -> {
                                rlLoading.invisible()
                                CustomDialog.getInstance()
                                    .setTitle(getString(R.string.warning_title))
                                    .setDescription(getString(R.string.error_invalid_room))
                                    .setPositiveButton(getString(R.string.refresh)) {
                                        lobbyViewModel.getRooms()
                                    }
                                    .setPositiveButtonDismissOnClicked(true)
                                    .show(supportFragmentManager, CustomDialog::class.java.simpleName)
                            }
                            else -> {}
                        }
                    }
                }
            }
            lobbyViewModel.joinLobbyStatus.observe(this@LobbyActivity) { event ->
                if (event.hasBeenHandled.not()) {
                    event.getContentIfNotHandled()?.let { username ->
                        rlLoading.invisible()
                        tvMessage.showAnimationText(getString(R.string.joined_lobby, username))
                    }
                }
            }
            lobbyViewModel.leaveRoomStatus.observe(this@LobbyActivity) {
                lobbyViewModel.getRooms()
            }
        }
    }

    companion object {
        fun getIntent(context: Context, username: String): Intent {
            val intent = Intent(context, LobbyActivity::class.java)
            intent.putExtra(Constants.KEY_USERNAME, username)
            return intent
        }
    }

    override fun onDestroy() {
        lobbyViewModel.outLobby()
        super.onDestroy()
    }
}