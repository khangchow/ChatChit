package com.dhk.chatchit.ui.chat_room

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.dhk.chatchit.R
import com.dhk.chatchit.databinding.ItemMessageReceiveBinding
import com.dhk.chatchit.databinding.ItemMessageSendBinding
import com.dhk.chatchit.databinding.ItemNotificationBinding
import com.dhk.chatchit.extension.hide
import com.dhk.chatchit.extension.invisible
import com.dhk.chatchit.extension.show
import com.dhk.chatchit.extension.showWithMessageStatus
import com.dhk.chatchit.model.Message
import com.dhk.chatchit.model.MessageStatus
import com.dhk.chatchit.model.toViewType
import com.dhk.chatchit.other.Constants
import com.dhk.chatchit.other.Resources

class ChatAdapter(
    var messages: MutableList<Message>
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

    fun addNewMessage(newMessage: Message) {
        messages.add(newMessage)
        notifyItemChanged(messages.size - 1)
    }

    fun updateMessageStatus(mes: Message) {
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
    fun bind(data: Message) {
        binding.apply {
            tvUsername.text = data.username
            tvSending.showWithMessageStatus(data.status)
            if (data.isImage) {
                flMessage.hide()
                when (data.status) {
                    MessageStatus.FAILED, MessageStatus.SENDING -> {
                        Glide.with(root.context).load(data.message.toUri()).into(ivImage)
                    }
                    MessageStatus.COMPLETED -> {
                        Glide.with(root.context)
                            .load(Constants.BASE_URL + "/" + data.message)
                            .listener(object : RequestListener<Drawable> {
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    ivImage.setImageURI(data.tempUri.toUri())
                                    vErrorImage.show()
                                    data.status = MessageStatus.LOAD_IMAGE_FAILED
                                    tvSending.text = Resources.getString(R.string.load_image_error)
                                    tvSending.show()
                                    return true
                                }

                                override fun onResourceReady(
                                    resource: Drawable?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    dataSource: DataSource?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    vErrorImage.invisible()
                                    return false
                                }
                            })
                            .into(ivImage)
                    }
                    else -> {}
                }
                flImage.show()
            } else {
                flImage.hide()
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
                MessageStatus.LOAD_IMAGE_FAILED -> {
                    tvSending.text = Resources.getString(R.string.load_image_error)
                    tvSending.show()
                }
            }
        }
    }
}

class MessageReceiveViewHolder(
    private val binding: ItemMessageReceiveBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(data: Message) {
        binding.apply {
            tvUsername.text = data.username
            tvSending.showWithMessageStatus(data.status)
            if (data.isImage) {
                flMessage.hide()
                Glide.with(root.context).load(Constants.BASE_URL + "/" + data.message).into(ivImage)
                ivImage.show()
            } else {
                flMessage.show()
                tvMessage.text = data.message
                ivImage.hide()
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
                else -> {}
            }
        }
    }
}

class NotificationViewHolder(
    private val binding: ItemNotificationBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(data: Message) {
        binding.tvNotification.text = data.message
    }
}
