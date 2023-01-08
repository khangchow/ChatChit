package com.dhk.chatchit.ui.lobby

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhk.chatchit.R
import com.dhk.chatchit.databinding.ActivityLobbyBinding
import com.dhk.chatchit.extension.*
import com.dhk.chatchit.other.Constants
import com.dhk.chatchit.other.Resource
import com.dhk.chatchit.ui.chat_room.ChatActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class LobbyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLobbyBinding
    private val lobbyViewModel: LobbyViewModel by viewModel()

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
                showAlertDialog(
                    alertTitle = getString(R.string.new_room),
                    positiveLabel = getString(R.string.create),
                    negativeLabel = getString(R.string.cancel),
                    positiveClick = { lobbyViewModel.newRoom(it) },
                    negativeClick = { },
                    alertMessage = ""
                )
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
                        resource.data?.let { rooms ->
                            (rvRooms.adapter as RoomAdapter).setListObject(rooms)
                        }
                        swipeRefreshLayout.isRefreshing = false
                    }
                    else -> Unit
                }
            }
            lobbyViewModel.createRoomStatus.observe(this@LobbyActivity) { event ->
                if (event.hasBeenHandled.not()) {
                    event.getContentIfNotHandled()?.let { resource ->
                        when (resource) {
                            is Resource.Success -> {
                                rlLoading.invisible()
                                resource.data?.let { roomName ->
                                    startActivity(ChatActivity.getIntent(this@LobbyActivity, roomName))
                                }
                            }
                            is Resource.Error -> showToast(getString(R.string.error_invalid_room_name))
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
                                showAlertDialog(
                                    alertTitle = getString(R.string.warning),
                                    positiveLabel = getString(R.string.refresh),
                                    negativeLabel = "",
                                    positiveClick = {
                                        lobbyViewModel.getRooms()
                                    },
                                    negativeClick = {},
                                    alertMessage = getString(R.string.error_invalid_room),
                                    showEditText = false
                                )
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
        }
    }

    private fun showToastError() {
        showToast(getString(R.string.common_error))
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