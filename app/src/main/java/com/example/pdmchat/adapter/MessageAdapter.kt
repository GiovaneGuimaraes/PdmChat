package com.example.pdmchat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.pdmchat.R
import com.example.pdmchat.model.Message

class MessageAdapter(context: Context, private val messageList: MutableList<Message>) :
    ArrayAdapter<Message>(context, R.layout.tile_message, messageList) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // pegar a mensagem no data source
        val message = messageList[position]
        var messageTileView = convertView
        if (messageTileView == null) {
            messageTileView = LayoutInflater.from(context).inflate(
                R.layout.tile_message,
                parent,
                false
            ).apply {
                tag = TileMessageHolder(
                    findViewById(R.id.nameTv),
                    findViewById(R.id.messageTv)
                )
            }
        }

        // colocar os valores de mensagem na célula
        (messageTileView?.tag as TileMessageHolder).apply {
            nameTv.text = message.sender
            messageTv.text = message.message
        }

        return messageTileView
    }

    private data class TileMessageHolder(val nameTv: TextView, val messageTv: TextView)
}