package com.dhk.chatchit.ui.chat_room

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dhk.chatchit.R
import com.dhk.chatchit.databinding.ItemMessageReceiveBinding
import com.dhk.chatchit.databinding.ItemMessageSendBinding
import com.dhk.chatchit.databinding.ItemNotificationBinding
import com.dhk.chatchit.extension.hide
import com.dhk.chatchit.extension.show
import com.dhk.chatchit.extension.showWithMessageStatus
import com.dhk.chatchit.model.MessageModel
import com.dhk.chatchit.model.MessageStatus
import com.dhk.chatchit.model.toViewType
import com.dhk.chatchit.utils.Resources

class ChatAdapter(
    private val messages: MutableList<MessageModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val MESSAGE_SEND = 1
        const val MESSAGE_RECEIVE = 2
        const val NOTIFICATION = 3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            MESSAGE_RECEIVE -> MessageReceiveViewHolder(ItemMessageReceiveBinding.inflate(
                LayoutInflater.from(parent.context), parent, false))
            MESSAGE_SEND -> MessageSendViewHolder(ItemMessageSendBinding.inflate(
                LayoutInflater.from(parent.context), parent, false))
            else -> NotificationViewHolder(ItemNotificationBinding.inflate(
                LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (messages[position].type.toViewType()) {
            MESSAGE_RECEIVE -> (holder as MessageReceiveViewHolder).bind(messages[position])
            MESSAGE_SEND -> (holder as MessageSendViewHolder).bind(messages[position])
            else -> (holder as NotificationViewHolder).bind(messages[position])
        }
    }

    override fun getItemCount() = messages.size

    override fun getItemViewType(position: Int): Int {
        return messages[position].type.toViewType()
    }

    fun addNewMessage(newMessage: MessageModel) {
        messages.add(newMessage)
        notifyItemChanged(messages.size - 1)
    }

    fun updateMessageStatus(mes: MessageModel) {
        messages.apply {
            indexOfFirst { it.messageId == mes.messageId }.let { index ->
                set(index, mes)
                this@ChatAdapter.notifyItemChanged(index)
            }
        }
    }

}

class MessageSendViewHolder(
    private val binding: ItemMessageSendBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(data: MessageModel) {
        binding.apply {
            tvUsername.text = data.username
            tvSending.showWithMessageStatus(data.status)
            if (data.isImage) {
                flMessage.hide()
                Glide.with(root.context).load(
                    data.message.apply {
                        if (data.status == MessageStatus.SENDING) toUri()
                    }
                ).into(ivImage)
                ivImage.show()
            } else {
                ivImage.hide()
                tvMessage.text = data.message
                flMessage.show()
            }
            when (data.status) {
                MessageStatus.SENDING -> {
                    tvSending.text = Resources.getString(R.string.message_status_sending)
                    tvSending.show()
                }
                MessageStatus.COMPLETED -> tvSending.hide()
                MessageStatus.FAILED -> {
                    tvSending.text = Resources.getString(R.string.message_status_failed)
                    tvSending.show()
                }
            }
        }
    }
}

class MessageReceiveViewHolder(
    private val binding: ItemMessageReceiveBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(data: MessageModel) {
        binding.apply {
            tvUsername.text = data.username
            tvSending.showWithMessageStatus(data.status)
            if (data.isImage) {
                flMessage.hide()
                Glide.with(root.context).load(data.message) to ivImage
                ivImage.show()
            } else {
                ivImage.hide()
                tvMessage.text = data.message
                flMessage.show()
            }
            when (data.status) {
                MessageStatus.SENDING -> {
                    tvSending.text = Resources.getString(R.string.message_status_sending)
                    tvSending.show()
                }
                MessageStatus.COMPLETED -> tvSending.hide()
                MessageStatus.FAILED -> {
                    tvSending.text = Resources.getString(R.string.message_status_failed)
                    tvSending.show()
                }
            }
        }
    }
}

class NotificationViewHolder(
    private val binding: ItemNotificationBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(data: MessageModel) {
        binding.tvNotification.text = data.message
    }
}
