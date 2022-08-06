package com.dhk.chatchit.ui.lobby

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhk.chatchit.R
import com.dhk.chatchit.adapter.RoomAdapter
import com.dhk.chatchit.adapter.base.ItemOnClick
import com.dhk.chatchit.databinding.ActivityLobbyBinding
import com.dhk.chatchit.model.RoomStatus
import com.dhk.chatchit.ui.MainActivity
import com.dhk.chatchit.utils.*
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
                itemOnClick = object : ItemOnClick<RoomStatus> {
                    override fun onClick(
                        view: View?,
                        idViewClick: Int?,
                        dataClicked: RoomStatus?
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
                    alertTitle = getStringById(R.string.new_room),
                    positiveLabel = getStringById(R.string.create),
                    negativeLabel = getStringById(R.string.cancel),
                    positiveClick = {
                        if (it.isEmpty()) showToast(getStringById(R.string.err_empty_name))
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
                        showToast(getStringById(R.string.created_room))
                        startActivity(MainActivity.getIntent(this@LobbyActivity, lobbyViewModel.room))
                    }
                    is LobbyAction.ValidRoomToJoin -> startActivity(MainActivity.getIntent(this@LobbyActivity, action.roomName))
                    is LobbyAction.ErrorInvalidRoom -> showAlertDialog(
                        alertTitle = getStringById(R.string.warning),
                        positiveLabel = getStringById(R.string.refresh),
                        negativeLabel = "",
                        positiveClick = {
                            lobbyViewModel.getRooms()
                        },
                        negativeClick = {},
                        alertMessage = getStringById(R.string.error_invalid_room),
                        showEditText = false
                    )
                    is LobbyAction.ErrorRepeatedRoomName -> showToast(getStringById(R.string.error_repeated_room_name))
                    is LobbyAction.JoinedLobby -> {
                        tvMessage.showAnimationText(action.username)
                        rlLoading.invisible()
                    }
                    is LobbyAction.LeftRoom -> {
                        showToast(getStringById(R.string.left_room))
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
        super.onDestroy()
        lobbyViewModel.outLobby()
    }
}