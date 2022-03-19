package com.dhk.chatchit.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.chow.chinesedicev2.adapter.ChatAdapter
import com.chow.chinesedicev2.local.AppPrefs
import com.chow.chinesedicev2.model.User
import com.chow.chinesedicev2.utils.KeyboardUtils
import com.dhk.chatchit.R
import com.dhk.chatchit.model.Message
import com.dhk.chatchit.utils.Constants
import com.dhk.chatchit.databinding.ActivityMainBinding
import com.dhk.chatchit.utils.showAnimationText
import com.dhk.chatchit.viewmodel.ChatViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val chatViewModel: ChatViewModel by viewModel()
    private val appPrefs: AppPrefs by inject()
    private var chatList = mutableListOf<Message>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setUpViewModel()

        setUpChatRecycleView()

        binding.apply {
            btnBack.setOnClickListener {
                finish()
            }

            btnSend.setOnClickListener {
                if (TextUtils.isEmpty(etMessage.text)) {
                    Toast.makeText(
                        this@MainActivity,
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
        }
    }

    fun setUpChatRecycleView() {
        binding.apply {
            rvChat.adapter = ChatAdapter(
                dataList = listOf(),
                appPrefs = appPrefs
            )

            rvChat.layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    @SuppressLint("SetTextI18n")
    fun setUpViewModel() {
        chatViewModel.listenUserState()

        chatViewModel.listenChatEvent()

        binding.apply {
            chatViewModel.userState.observe(this@MainActivity) {
                tvMessage.showAnimationText("${it.username} ${it.state}")
            }
        }
    }

    companion object {
        fun getIntent(context: Context): Intent {
            val intent = Intent(context, MainActivity::class.java)

            return intent
        }
    }
}