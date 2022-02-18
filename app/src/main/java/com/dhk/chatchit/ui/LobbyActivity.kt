package com.dhk.chatchit.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dhk.chatchit.R
import com.dhk.chatchit.utils.Constants

class LobbyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)
    }

    companion object {
        fun getIntent(context: Context, username: String): Intent {
            val intent = Intent(context, MainActivity::class.java)

            intent.putExtra(Constants.KEY_USERNAME, username)

            return intent
        }
    }
}