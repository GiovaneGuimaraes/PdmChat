package com.example.pdmchat.controller

import com.example.pdmchat.SendMessageActivity
import com.example.pdmchat.model.Message
import com.example.pdmchat.model.MessageDao
import com.example.pdmchat.model.MessageDaoRtImpl
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SaveMessageController(private val mainActivity: SendMessageActivity) {
    private val messageDaoImpl: MessageDao = MessageDaoRtImpl()

    fun insertMessage(message: Message) {
        GlobalScope.launch {
            messageDaoImpl.createMessage(message)
        }
    }
}