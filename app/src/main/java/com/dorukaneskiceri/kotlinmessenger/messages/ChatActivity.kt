package com.dorukaneskiceri.kotlinmessenger.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.dorukaneskiceri.kotlinmessenger.R
import com.dorukaneskiceri.kotlinmessenger.models.ChatMessage
import com.dorukaneskiceri.kotlinmessenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_chat.view.*
import kotlinx.android.synthetic.main.chat_screen_from.view.*
import kotlinx.android.synthetic.main.chat_screen_to.view.*
import java.util.*

class ChatActivity : AppCompatActivity() {

    private val registerAuth = FirebaseAuth.getInstance()
    private val adapter = GroupAdapter<GroupieViewHolder>()
    var toUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        recyclerview_chat_screen.layoutManager = LinearLayoutManager(this)
        recyclerview_chat_screen.adapter = adapter

        toUser = intent.getParcelableExtra("user")
        supportActionBar?.title = toUser?.username

        //setupDummyData()
        listenForMessages()

        sendButton.setOnClickListener {
            performSendMessages()
        }
    }

    private fun listenForMessages(){
        val database = FirebaseDatabase.getInstance().getReference("/messages")

        database.addChildEventListener(object : ChildEventListener{

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                val currentUser = LatestMessagesActivity.currentUser
                if(chatMessage != null){
                    if(chatMessage.fromId == registerAuth.uid){
                        adapter.add(ChatItemFrom(chatMessage.message, currentUser!!))
                        //adapter.add(ChatItemFrom(chatMessage.message,))
                    }
                    else{
                        adapter.add(ChatItemTo(chatMessage.message, toUser!!))
                    }

                }

            }

            override fun onCancelled(error: DatabaseError) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

        })
    }

    private fun performSendMessages(){
        val database = FirebaseDatabase.getInstance().getReference("/messages").push()

        val textMessage = messageText_chat_activity.text.toString()
        val fromId = registerAuth.uid
        val userKey = intent.getParcelableExtra<User>("user")
        val toId = userKey.uid

        if(fromId == null) return
        val message = ChatMessage(database.key!!, textMessage, fromId, toId, System.currentTimeMillis() / 1000)

        database.setValue(message).addOnSuccessListener {
            println("Message save successful")
        }.addOnFailureListener {
            println(it.localizedMessage.toString())
        }
    }

    class ChatItemFrom(val text: String, val user: User): Item<GroupieViewHolder>(){
        override fun getLayout(): Int {
            return R.layout.chat_screen_from
        }

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.messageText_from_row.text = text

            val uri = user.imageUrl
            val circleImageView = viewHolder.itemView.circleImageView_from
            Picasso.get().load(uri).into(circleImageView)
        }
    }

    class ChatItemTo(val text: String, val user: User): Item<GroupieViewHolder>(){
        override fun getLayout(): Int {
            return R.layout.chat_screen_to
        }

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.messageText_to_row.text = text

            val uri = user.imageUrl
            val circleImageView = viewHolder.itemView.circleImageView_to
            Picasso.get().load(uri).into(circleImageView)
        }
    }

}
