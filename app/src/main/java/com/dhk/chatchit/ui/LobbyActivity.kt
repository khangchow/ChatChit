package com.dhk.chatchit.ui

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chow.chinesedicev2.adapter.RoomAdapter
import com.chow.chinesedicev2.adapter.base.ItemOnClick
import com.dhk.chatchit.R
import com.dhk.chatchit.databinding.ActivityLobbyBinding
import com.dhk.chatchit.model.RoomStatus
import com.dhk.chatchit.utils.Constants
import com.dhk.chatchit.utils.showAlertDialog
import com.dhk.chatchit.utils.showAnimationText
import com.dhk.chatchit.viewmodel.LobbyViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class LobbyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLobbyBinding
    private val lobbyViewModel: LobbyViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLobbyBinding.inflate(layoutInflater)

        setContentView(binding.root)

        lobbyViewModel.joinLobby(intent.getStringExtra(Constants.KEY_USERNAME).toString())

        setUpRecView()
        setUpViewModel()

        binding.apply {
            btnNew.setOnClickListener {
                showAlertDialog(
                    alertTitle = getString(R.string.new_room),
                    positiveLabel = getString(R.string.create),
                    negativeLabel = getString(R.string.cancel),
                    positiveClick = {
                        if (it.isEmpty()) Toast.makeText(
                            this@LobbyActivity,
                            getString(R.string.err_empty_name),
                            Toast.LENGTH_SHORT
                        ).show()
                        else {
                            lobbyViewModel.newRoom(it)
                        }
                    },
                    negativeClick = {

                    },
                    alertMessage = ""
                )
            }
        }
    }

    fun setUpRecView() {
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
                                startActivity(MainActivity.getIntent(this@LobbyActivity, dataClicked!!.name))
                            }
                        }
                    }
                }
            )

            rvRooms.layoutManager = LinearLayoutManager(this@LobbyActivity)
        }
    }

    fun setUpViewModel() {
        binding.apply {
            lobbyViewModel.message.observe(this@LobbyActivity) {
                if (it.error.isEmpty()) {
                    if (it.data.equals("Created New Room")) {
                        Toast.makeText(this@LobbyActivity, it.data, Toast.LENGTH_SHORT).show()

                        lobbyViewModel.getRooms()

                        startActivity(MainActivity.getIntent(this@LobbyActivity, lobbyViewModel.room))
                    }else {
                        rlLoading.visibility = View.INVISIBLE

                        tvMessage.showAnimationText(it.data)
                    }
                }
                else Toast.makeText(this@LobbyActivity, it.error, Toast.LENGTH_SHORT).show()
            }

            lobbyViewModel.rooms.observe(this@LobbyActivity) {
                if (it.error.isEmpty()) {
                    Log.d("KHANG", "setUpViewModel: "+it.data.toString())

                    (rvRooms.adapter as RoomAdapter).setListObject(it.data)

                    swipeRefreshLayout.isRefreshing = false
                }
            }

        }

        lobbyViewModel.getRooms()
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