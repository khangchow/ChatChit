package com.chow.chinesedicev2.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.content.res.AppCompatResources

import com.chow.chinesedicev2.adapter.base.BaseAdapters
import com.chow.chinesedicev2.adapter.base.ItemOnClick

import com.chow.chinesedicev2.local.AppPrefs
import com.chow.chinesedicev2.model.User
import com.dhk.chatchit.model.Message
import com.dhk.chatchit.R
import com.dhk.chatchit.databinding.ListChatBinding
import com.dhk.chatchit.utils.Constants
import com.google.gson.Gson


class ChatAdapter(
    dataList: List<Message> = listOf(),
    private val itemOnClick: ItemOnClick<Message>? = null,
    private val appPrefs: AppPrefs
) : BaseAdapters<Message, ListChatBinding>(dataList) {

    @SuppressLint("SetTextI18n")
    override fun onBindViewHold(
        position: Int,
        dataItem: Message,
        binding: ListChatBinding
    ) {
        binding.apply {
            tvUsername.text = dataItem.username

            tvMessage.text = dataItem.message

            val paramsUsername = tvUsername.layoutParams as RelativeLayout.LayoutParams
            val paramsMsg = tvMessage.layoutParams as RelativeLayout.LayoutParams

            Log.d(
                "CHAT",
                appPrefs.getString(Constants.KEY_USER_DATA).toString() + " " + dataItem.id
            )
            if (Gson().fromJson(
                    appPrefs.getString(Constants.KEY_USER_DATA),
                    User::class.java
                ).id == dataItem.id
            ) {
                paramsUsername.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                paramsMsg.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)

                tvMessage.background = AppCompatResources.getDrawable(
                    binding.root.context, R.drawable.chat_message_me
                )

                tvMessage.setTextColor(Color.GREEN)
            } else {
                paramsUsername.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                paramsMsg.addRule(RelativeLayout.ALIGN_PARENT_LEFT)

                tvMessage.background = AppCompatResources.getDrawable(
                    binding.root.context, R.drawable.chat_message_others
                )

                tvMessage.setTextColor(Color.WHITE)
            }

            tvUsername.layoutParams = paramsUsername
            tvMessage.layoutParams = paramsMsg
        }
    }

    override fun getViewBinding(viewGroup: ViewGroup) =
        ListChatBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
}