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
                                Toast.makeText(
                                    this@LobbyActivity,
                                    "Clicked ${dataClicked?.name}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            )

            rvRooms.layoutManager = LinearLayoutManager(this@LobbyActivity)
        }
    }

    fun setUpViewModel() {
        lobbyViewModel.rooms.observe(this) {
            binding.apply {
               if (it.error.isEmpty()) {
                   Log.d("ROOM", it.toString())
                   Toast.makeText(this@LobbyActivity, it.toString(), Toast.LENGTH_SHORT).show()
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