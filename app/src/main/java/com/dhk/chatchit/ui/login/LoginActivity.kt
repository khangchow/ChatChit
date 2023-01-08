package com.dhk.chatchit.ui.login

import android.os.Bundle
import com.dhk.chatchit.R
import com.dhk.chatchit.base.BaseActivity
import com.dhk.chatchit.databinding.ActivityLoginBinding
import com.dhk.chatchit.extension.showToast
import com.dhk.chatchit.other.Resource
import com.dhk.chatchit.ui.lobby.LobbyActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : BaseActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            btnJoin.setOnClickListener {
                loginViewModel.login(etUsername.text.toString())
            }
        }
        loginViewModel.loginStatus.observe(this) { event ->
            if (event.hasBeenHandled.not()) {
                event.getContentIfNotHandled()?.let { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            resource.data?.let { username ->
                                startActivity(
                                    LobbyActivity.getIntent(this@LoginActivity, username)
                                )
                            }
                        }
                        is Resource.Error -> showToast(getString(R.string.empty_username_warning))
                        else -> {}
                    }
                }
            }
        }
    }
}