package com.dhk.chatchit.ui.lobby

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhk.chatchit.R
import com.dhk.chatchit.base.ItemOnClick
import com.dhk.chatchit.databinding.ActivityLobbyBinding
import com.dhk.chatchit.extension.invisible
import com.dhk.chatchit.extension.showAlertDialog
import com.dhk.chatchit.extension.showAnimationText
import com.dhk.chatchit.extension.showToast
import com.dhk.chatchit.model.RoomStatusModel
import com.dhk.chatchit.ui.chat_room.ChatActivity
import com.dhk.chatchit.utils.Constants
import org.koin.androidx.viewmodel.ext.android.viewModel

class LobbyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLobbyBinding
    private val lobbyViewModel: LobbyViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLobbyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lobbyViewModel.joinLobby(intent.getStringExtra(Constants.KEY_USERNAME).toString())
        lobbyViewModel.getRooms()
        setUpView()
        setUpViewModel()
    }

    private fun setUpView() {
        binding.apply {
            swipeRefreshLayout.setOnRefreshListener {
                lobbyViewModel.getRooms()
            }
            rvRooms.adapter = RoomAdapter(
                dataList = listOf(),
                itemOnClick = object : ItemOnClick<RoomStatusModel> {
                    override fun onClick(
                        view: View?,
                        idViewClick: Int?,
                        dataClicked: RoomStatusModel?
                    ) {
                        when (idViewClick) {
                            R.id.parent -> {
                                lobbyViewModel.checkRoom(dataClicked!!.name)
                            }
                        }
                    }
                }
            )
            rvRooms.layoutManager = LinearLayoutManager(this@LobbyActivity)
            btnNew.setOnClickListener {
                showAlertDialog(
                    alertTitle = getString(R.string.new_room),
                    positiveLabel = getString(R.string.create),
                    negativeLabel = getString(R.string.cancel),
                    positiveClick = {
                        if (it.isEmpty()) showToast(getString(R.string.err_empty_name))
                        else lobbyViewModel.newRoom(it)
                    },
                    negativeClick = {},
                    alertMessage = ""
                )
            }
        }
    }

    private fun setUpViewModel() {
        binding.apply {
            lobbyViewModel.action.observe(this@LobbyActivity) { action ->
                when (action) {
                    is LobbyAction.GetRooms -> {
                        (rvRooms.adapter as RoomAdapter).setListObject(action.rooms)
                        swipeRefreshLayout.isRefreshing = false
                    }
                    is LobbyAction.NewRoomCreated -> {
                        showToast(getString(R.string.created_room))
                        startActivity(ChatActivity.getIntent(this@LobbyActivity, lobbyViewModel.room))
                    }
                    is LobbyAction.ValidRoomToJoin -> startActivity(ChatActivity.getIntent(this@LobbyActivity, action.roomName))
                    is LobbyAction.ErrorInvalidRoom -> showAlertDialog(
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
                    is LobbyAction.ErrorRepeatedRoomName -> showToast(getString(R.string.error_repeated_room_name))
                    is LobbyAction.JoinedLobby -> {
                        tvMessage.showAnimationText(getString(R.string.joined_lobby, action.username))
                        rlLoading.invisible()
                    }
                    is LobbyAction.LeftRoom -> {
                        showToast(getString(R.string.left_room))
                        lobbyViewModel.getRooms()
                    }
                    else -> Unit
                }
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