package com.dhk.chatchit.ui.lobby

import android.view.LayoutInflater
import android.view.ViewGroup
import com.dhk.chatchit.base.BaseAdapters
import com.dhk.chatchit.base.ItemOnClick
import com.dhk.chatchit.databinding.ListRoomBinding
import com.dhk.chatchit.model.RoomStatusModel


class RoomAdapter(
    dataList: List<RoomStatusModel> = listOf(),
    private val itemOnClick: ItemOnClick<RoomStatusModel>? = null,
) : BaseAdapters<RoomStatusModel, ListRoomBinding>(dataList) {

    override fun onBindViewHold(
        position: Int,
        dataItem: RoomStatusModel,
        binding: ListRoomBinding
    ) {
        binding.apply {
            tvName.text = dataItem.name
            tvActive.text = dataItem.activeUser.toString()
            parent.setOnClickListener {
                itemOnClick?.onClick(it, it.id, dataItem)
            }
        }
    }

    override fun getViewBinding(viewGroup: ViewGroup) =
        ListRoomBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
}