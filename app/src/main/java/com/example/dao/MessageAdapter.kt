package com.example.dao

import android.icu.text.SimpleDateFormat
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weixin_ui.R
import java.util.*


class MessageAdapter(private val messageList: List<MessageList>, private val currentUser: String) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageTextView: TextView = itemView.findViewById(R.id.message)
        val timestampTextView: TextView = itemView.findViewById(R.id.time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val layoutId = if (viewType == VIEW_TYPE_ME) R.layout.item_message_me else R.layout.item_message
        val itemView = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return MessageViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messageList[position]
        holder.messageTextView.text = message.message
        val timestamp = message.timestamp.toLongOrNull()
        if (timestamp != null) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val formattedTimestamp = dateFormat.format(Date(timestamp))
            holder.timestampTextView.text = formattedTimestamp
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {
        // 根据发送者返回不同的视图类型
        val sender = messageList[position].sender
        return if (sender == currentUser) VIEW_TYPE_ME else VIEW_TYPE_FRIEND
    }

    companion object {
        private const val VIEW_TYPE_ME = 0
        private const val VIEW_TYPE_FRIEND = 1
    }
}

