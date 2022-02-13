package com.dhk.chatchit.ui

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.dhk.chatchit.databinding.ActivityLoginBinding
import com.dhk.chatchit.ui.base.BaseActivity

class LoginActivity : BaseActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.apply {
            btnJoin.setOnClickListener {
                if (TextUtils.isEmpty(etUsername.text)) Toast.makeText(
                    this@LoginActivity,
                    "Please enter username!",
                    Toast.LENGTH_SHORT
                ).show()
                else startActivity(
                    MainActivity.getIntent(
                        this@LoginActivity,
                        etUsername.text.toString()
                    )
                )
            }
        }
    }
}