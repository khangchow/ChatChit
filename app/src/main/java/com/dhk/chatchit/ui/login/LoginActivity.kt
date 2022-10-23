package com.dhk.chatchit.ui.login

import android.os.Bundle
import android.text.TextUtils
import com.dhk.chatchit.R
import com.dhk.chatchit.databinding.ActivityLoginBinding
import com.dhk.chatchit.base.BaseActivity
import com.dhk.chatchit.ui.lobby.LobbyActivity
import com.dhk.chatchit.utils.showToast

class LoginActivity : BaseActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.apply {
            btnJoin.setOnClickListener {
                if (TextUtils.isEmpty(etUsername.text)) showToast(getString(R.string.empty_username_warning))
                else startActivity(
                        LobbyActivity.getIntent(
                            this@LoginActivity,
                            etUsername.text.toString()
                        )
                    )
            }
        }
    }
}