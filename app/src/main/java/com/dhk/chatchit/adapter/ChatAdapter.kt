package com.chow.chinesedicev2.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.chow.chinesedicev2.adapter.base.BaseAdapters
import com.dhk.chatchit.adapter.base.ItemOnClick

import com.chow.chinesedicev2.local.AppPrefs
import com.chow.chinesedicev2.model.User
import com.dhk.chatchit.model.Message
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
            if (dataItem.type == Constants.TYPE_NOTIFICATION) {
                viewMe.visibility = View.GONE
                viewOther.visibility = View.GONE

                tvNotification.visibility = View.VISIBLE

                tvNotification.text = "${dataItem.username} ${dataItem.message}"
            }else {
                tvNotification.visibility = View.GONE

                if (Gson().fromJson(
                        appPrefs.getString(Constants.KEY_USER_DATA),
                        User::class.java
                    ).id == dataItem.id
                ) {
                    viewMe.visibility = View.VISIBLE
                    viewOther.visibility = View.GONE

                    tvUsernameMe.text = dataItem.username
                    tvMessageMe.text = dataItem.message
                }else {
                    viewOther.visibility = View.VISIBLE
                    viewMe.visibility = View.GONE

                    tvUsernameOther.text = dataItem.username
                    tvMessageOther.text = dataItem.message
                }
            }
        }
    }

    override fun getViewBinding(viewGroup: ViewGroup) =
        ListChatBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
}