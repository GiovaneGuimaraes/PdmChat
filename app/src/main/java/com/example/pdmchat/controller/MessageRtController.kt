package com.example.pdmchat.controller

import com.example.pdmchat.MainActivity
import android.util.Log
import android.os.Message
import com.example.pdmchat.model.MessageDao
import com.example.pdmchat.model.MessageDaoRtImpl

class MessageRtController(private val mainActivity: MainActivity) {
    private val messageDaoImpl: MessageDao = MessageDaoRtImpl()

    fun getMessages() {
        val messageList = messageDaoImpl.retrieveMessages()

        if (messageList.isNotEmpty()) {
            val sortedChatList = messageList.sortedByDescending { it.getDateTime() }
            mainActivity.uiUpdaterHandler.sendMessage(
                Message.obtain().apply {
                    data.putParcelableArrayList(
                        "MESSAGE_ARRAY",
                        ArrayList(sortedChatList)
                    )
                }
            )
        }
    }
}