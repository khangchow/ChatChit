package com.dhk.chatchit.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup

import com.chow.chinesedicev2.adapter.base.BaseAdapters
import com.dhk.chatchit.adapter.base.ItemOnClick

import com.dhk.chatchit.databinding.ListRoomBinding
import com.dhk.chatchit.model.RoomStatus


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