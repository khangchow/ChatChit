package com.dhk.chatchit.ui

import android.os.Bundle
import com.dhk.chatchit.databinding.ActivityLoginBinding
import com.dhk.chatchit.ui.base.BaseActivity

class LoginActivity : BaseActivity() {
    private lateinit var bindind: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindind = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(bindind.root)

        
    }
}