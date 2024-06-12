package com.example.pdmchat.model
import android.util.Log
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MessageDaoRtImpl : MessageDao {
    companion object {
        private const val CHAT_LIST_ROOT_NODE = "message"
    }

    private val messageRtDbFbReference = Firebase.database.getReference(CHAT_LIST_ROOT_NODE)
    private var isFirstValueEvent = true


    private val messageList = mutableListOf<Message>()

    init {

        messageRtDbFbReference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                if (message != null && !messageList.contains(message)) {
                    messageList.add(message)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                if (message != null) {
                    val index = messageList.indexOfFirst { it.id == message.id }
                    if (index != -1) {
                        messageList[index] = message
                    }
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val chat = snapshot.getValue(Message::class.java)
                if (chat != null) {
                    messageList.remove(chat)
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                // NSA
            }

            override fun onCancelled(error: DatabaseError) {
                // NSA
            }
        })

        messageRtDbFbReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (isFirstValueEvent) {
                    isFirstValueEvent = false
                    val messages = snapshot.children.mapNotNull { it.getValue(Message::class.java) }
                    if (messages.isNotEmpty()) {
                        messageList.addAll(messages.filterNot { messageList.contains(it) })
                        for (message in messages) {
                            println(message)
                        }
                    } else {
                        println("Nenhuma mensagem encontrada.")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // NSA
            }
        })
    }

    override fun createMessage(message: Message): Int {
        val newMessageRef = messageRtDbFbReference.push()
        val id = newMessageRef.key ?: return -1
        message.id = id
        newMessageRef.setValue(message)
        Log.d("Firebase", "Enviando message para o Firebase: $message")
        return 1
    }

    override fun retrieveMessages(): MutableList<Message> {
        return messageList
    }

    private fun createOrUpdateMessage(message: Message) {
        Log.d("Firebase", "Atualizando message no banco de dados: $message")
        messageRtDbFbReference.child(message.id.toString()).setValue(message)
    }
}