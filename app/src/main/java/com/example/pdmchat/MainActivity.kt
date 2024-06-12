package com.example.pdmchat

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pdmchat.adapter.MessageAdapter
import com.example.pdmchat.controller.MessageRtController
import com.example.pdmchat.databinding.ActivityMainBinding
import com.example.pdmchat.model.Message
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    companion object {
        const val GET_MESSAGES = 1
        const val GET_CONTACTS_INTERVAL = 2000L
    }
    private lateinit var auth: FirebaseAuth
    private lateinit var sendMessageBt: Button

    private val amb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val messageList: MutableList<Message> = mutableListOf()

    private val messageController: MessageRtController by lazy {
        MessageRtController(this)
    }

    private val messageAdapter: MessageAdapter by lazy {
        MessageAdapter(this, messageList)
    }

    private lateinit var messageLv: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar o Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Verificar se o usuário está logado
        val currentUser = auth.currentUser
        if (currentUser == null) {
            // Usuário não está logado, redirecionar para LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            setContentView(amb.root)
            sendMessageBt = findViewById(R.id.sendMessageBt)

            sendMessageBt.setOnClickListener {
                startActivity(Intent(this, SendMessageActivity::class.java))
            }

            uiUpdaterHandler.apply {
                sendMessageDelayed(
                    android.os.Message.obtain().apply {
                        what = GET_MESSAGES
                    },
                    GET_CONTACTS_INTERVAL
                )
            }

            amb.messageLv.adapter = messageAdapter
        }
    }

    val uiUpdaterHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: android.os.Message) {
            super.handleMessage(msg)
            // Busca contatos ou atualiza a lista de acordo com o tipo de mensagem
            if (msg.what == GET_MESSAGES) {
                // Busca os contatos
                messageController.getMessages()
                sendMessageDelayed(
                    android.os.Message.obtain().apply {
                        what = GET_MESSAGES
                    },
                    GET_CONTACTS_INTERVAL
                )
            }
            else {
                val username = auth.currentUser?.email ?: ""
                msg.data.getParcelableArrayList<Message>("MESSAGE_ARRAY")?.let { _contactArray ->
                    updateMessagesList(_contactArray.toMutableList(), username)
                }
            }
        }
    }

    fun updateMessagesList(messages: MutableList<Message>, username: String) {
        val filteredChats = messages.filter { it.receiver == username }.sortedByDescending { it.getDateTime() }

        messageList.clear()
        messageList.addAll(filteredChats)
        messageAdapter.notifyDataSetChanged()
    }
}
