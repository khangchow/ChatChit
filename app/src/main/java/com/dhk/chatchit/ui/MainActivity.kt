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
        chatViewModel.listenPlayerState()

        chatViewModel.listenChatEvent()

        intent.getStringExtra(Constants.KEY_USERNAME)?.let { chatViewModel.joinChatRoom(it) }

        binding.apply {
            chatViewModel.userState.observe(this@MainActivity) {
                tvMessage.text = "${it.username} ${it.state}"

                val animAppear = AlphaAnimation(0f, 1f)
                animAppear.duration = 1000L
                animAppear.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationRepeat(animation: Animation?) {
                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        val timer = object : CountDownTimer(1000, 200) {
                            override fun onTick(millisUntilFinished: Long) {}

                            override fun onFinish() {
                                val animDisappear = AlphaAnimation(1f, 0f)
                                animDisappear.duration = 1000L
                                animDisappear.setAnimationListener(object :
                                    Animation.AnimationListener {
                                    override fun onAnimationRepeat(animation: Animation?) {
                                    }

                                    override fun onAnimationEnd(animation: Animation?) {
                                        tvMessage.visibility = View.GONE
                                    }

                                    override fun onAnimationStart(animation: Animation?) {
                                    }

                                })

                                tvMessage.startAnimation(animDisappear)
                            }
                        }

                        timer.start()
                    }

                    override fun onAnimationStart(animation: Animation?) {
                    }

                })

                tvMessage.visibility = View.VISIBLE
                tvMessage.startAnimation(animAppear)
            }

            chatViewModel.chat.observe(this@MainActivity) {
                chatList.add(it)

                (rvChat.adapter as ChatAdapter).setListObject(chatList)

                rvChat.scrollToPosition(chatList.size - 1)
            }
        }
    }

    companion object {
        fun getIntent(context: Context, username: String): Intent {
            val intent = Intent(context, MainActivity::class.java)

            intent.putExtra(Constants.KEY_USERNAME, username)

            return intent
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        chatViewModel.outChatRoom()
    }
}