package com.chow.chinesedicev2.adapter.base

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseAdapters<T, B : ViewBinding>(
    dataDefaultList: List<T> = listOf(),
) : RecyclerView.Adapter<BaseAdapters<T, B>.BaseViewHolder>() {

    abstract fun getViewBinding(viewGroup: ViewGroup): B

    var dataList: List<T> = dataDefaultList

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): BaseViewHolder {
        return BaseViewHolder(getViewBinding(viewGroup))
    }

    override fun onBindViewHolder(viewHolder: BaseViewHolder, position: Int) {
        onBindViewHold(position, getItem(position), viewHolder.itemViewBinding)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun setListObject(listItem: List<T>?) {
        dataList = listItem ?: listOf()
        notifyDataSetChanged()
    }

    fun newChatMessage(listItem: List<T>?) {
        dataList = listItem ?: listOf()
        notifyItemInserted(itemCount-1)
    }
    
    fun updateDataList(position: Int, item: T) {
        dataList = dataList.toMutableList().apply {
            set(position, item)
        }
//        notifyItemChanged(position)
    }

    fun getItem(position: Int) = dataList[position]

    abstract fun onBindViewHold(position: Int, dataItem: T, binding: B)

    inner class BaseViewHolder(val itemViewBinding: B) :
        RecyclerView.ViewHolder(itemViewBinding.root)
}