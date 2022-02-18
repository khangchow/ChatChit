package com.chow.chinesedicev2.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
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
import com.dhk.chatchit.databinding.ListRoomBinding
import com.dhk.chatchit.model.RoomStatus
import com.dhk.chatchit.utils.Constants
import com.google.gson.Gson


class RoomAdapter(
    dataList: List<RoomStatus> = listOf(),
    private val itemOnClick: ItemOnClick<RoomStatus>? = null,
) : BaseAdapters<RoomStatus, ListRoomBinding>(dataList) {

    @SuppressLint("SetTextI18n")
    override fun onBindViewHold(
        position: Int,
        dataItem: RoomStatus,
        binding: ListRoomBinding
    ) {
        binding.apply {
            tvName.text = dataItem.name
            tvActive.text = dataItem.active.toString()
            parent.setOnClickListener {
                itemOnClick?.onClick(it, it.id, dataItem)
            }
        }
    }

    override fun getViewBinding(viewGroup: ViewGroup) =
        ListRoomBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
}