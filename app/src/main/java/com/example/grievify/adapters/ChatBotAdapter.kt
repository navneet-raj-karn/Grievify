package com.example.grievify.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.grievify.R

import com.example.grievify.adapters.ChatBotAdapter.ChatViewHolder
import com.example.grievify.models.Message

class ChatBotAdapter(private var activity: Activity, private var messageList: List<Message>) : RecyclerView.Adapter<ChatBotAdapter.ChatViewHolder>() {

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var messageReceive: TextView = itemView.findViewById(R.id.message_receive)
        var messageSend: TextView = itemView.findViewById(R.id.message_send)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(activity).inflate(R.layout.adapter_message_one, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message: String = messageList[position].message
        val isReceived: Boolean = messageList[position].isReceived
        if (isReceived) {
            holder.messageReceive.visibility = View.VISIBLE
            holder.messageSend.visibility = View.GONE
            holder.messageReceive.text = message
        } else {
            holder.messageSend.visibility = View.VISIBLE
            holder.messageReceive.visibility = View.GONE
            holder.messageSend.text = message
        }
    }

    override fun getItemCount(): Int {
        return messageList.count()
    }
}

