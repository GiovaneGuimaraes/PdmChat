package com.example.pdmchat

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pdmchat.controller.SaveMessageController
import com.example.pdmchat.model.Message
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class SendMessageActivity : AppCompatActivity() {

    private val messageController: SaveMessageController by lazy {
        SaveMessageController(this)
    }
    private lateinit var auth: FirebaseAuth
    private lateinit var sendMessageBt: Button
    private lateinit var textET: EditText
    private lateinit var emailReceiver: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_message)

        // Inicializar o Firebase Auth
        auth = FirebaseAuth.getInstance()

        sendMessageBt = findViewById(R.id.sendMessageButton)
        textET = findViewById(R.id.messageContentEditText)
        emailReceiver = findViewById(R.id.receiverEmailEditText)

        // Configurar o botão de envio de mensagem
        sendMessageBt.setOnClickListener {

            val currentUserEmail = auth.currentUser?.email ?: ""
            val receiverEmail = emailReceiver.text.toString()
            val messageContent = textET.text.toString()
            println(currentUserEmail)


            if (receiverEmail.isNotEmpty() && messageContent.isNotEmpty()) {
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                val currentDate = dateFormat.format(Date())
                val currentTime = timeFormat.format(Date())


                // Criar a mensagem
                val message = Message(sender = currentUserEmail, receiver = receiverEmail,
                    date = currentDate.toString(), time = currentTime.toString(), message = messageContent)

                messageController.insertMessage(message)
                Toast.makeText(this, "enviou a mensagem", Toast.LENGTH_SHORT).show()
                finish()
        } else{
                Toast.makeText(this, "caixa de texto ou de email vazia", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
