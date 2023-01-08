package com.dhk.chatchit.ui.lobby

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dhk.chatchit.databinding.ListRoomBinding
import com.dhk.chatchit.model.RoomStatusModel

class RoomAdapter(
    private var dataList: List<RoomStatusModel> = listOf(),
    private val onCLickedRoom: (String) -> Unit
) : RecyclerView.Adapter<RoomAdapter.RoomViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RoomViewHolder(
        ListRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        holder.binding.apply {
            dataList[position].let { data ->
                tvName.text = data.name
                tvActive.text = data.activeUser.toString()
                parent.setOnClickListener { data.name.run(onCLickedRoom) }
            }
        }
    }

    override fun getItemCount() = dataList.size

    fun setListObject(rooms: List<RoomStatusModel>) {
        dataList = rooms
        notifyDataSetChanged()
    }

    class RoomViewHolder(val binding: ListRoomBinding) : RecyclerView.ViewHolder(binding.root)
}
