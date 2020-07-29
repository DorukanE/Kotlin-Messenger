package com.dorukaneskiceri.kotlinmessenger.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.dorukaneskiceri.kotlinmessenger.Notifications.*
import com.dorukaneskiceri.kotlinmessenger.R
import com.dorukaneskiceri.kotlinmessenger.Fragments.APIService
import com.dorukaneskiceri.kotlinmessenger.models.ChatMessage
import com.dorukaneskiceri.kotlinmessenger.models.User
import com.dorukaneskiceri.kotlinmessenger.views.ChatItemFrom
import com.dorukaneskiceri.kotlinmessenger.views.ChatItemTo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_chat.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatActivity : AppCompatActivity() {

    private val registerAuth = FirebaseAuth.getInstance()
    private val adapter = GroupAdapter<GroupieViewHolder>()
    var toUser: User? = null
    var notify = false
    var apiService: APIService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        recyclerview_chat_screen.layoutManager = LinearLayoutManager(this)
        recyclerview_chat_screen.adapter = adapter

        apiService = Client.Client.getClient("https://fcm.googleapis.com/")!!.create(APIService::class.java)

        toUser = intent.getParcelableExtra("user")

        supportActionBar?.title = toUser?.username

        listenForMessages()

        sendButton.setOnClickListener {
            notify = true
            performSendMessages()
        }

        updateToken(FirebaseInstanceId.getInstance().token)
    }

    private fun updateToken(token: String?) {
        val ref = FirebaseDatabase.getInstance().reference.child("tokens")
        val token1 = Token(token!!)
        ref.child(registerAuth.uid!!).setValue(token1)
    }

    private fun listenForMessages(){
        val fromId = registerAuth.uid
        val toId = toUser?.uid
        val database = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

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

                recyclerview_chat_screen.scrollToPosition(adapter.itemCount - 1)
            }

            override fun onCancelled(error: DatabaseError) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}

        })
    }

    private fun performSendMessages(){

        val textMessage = messageText_chat_activity.text.toString()
        val fromId = registerAuth.uid
        val userKey = intent.getParcelableExtra<User>("user")
        val toId = userKey.uid

        //For From messages
        val database = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()

        if(fromId == null) return
        val message = ChatMessage(database.key!!, textMessage, fromId, toId, System.currentTimeMillis() / 1000)

        if(textMessage.isNotBlank()){
            database.setValue(message).addOnSuccessListener {
                messageText_chat_activity.text.clear()
                recyclerview_chat_screen.scrollToPosition(adapter.itemCount - 1)
            }.addOnFailureListener {
                println(it.localizedMessage.toString())
            }

            //For To messages
            val toDatabase = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

            toDatabase.setValue(message).addOnSuccessListener {
                println("Message save for other user is successful.")
            }.addOnFailureListener {
                println(it.localizedMessage.toString())
            }

            val latestMessagesRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
            latestMessagesRef.setValue(message).addOnSuccessListener {
            }.addOnFailureListener {
                println(it.localizedMessage.toString())
            }

            val latestMessagesRefTo = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
            latestMessagesRefTo.setValue(message).addOnSuccessListener {
            }.addOnFailureListener {
                println(it.localizedMessage.toString())
            }

            // Send push notifications using FCM
            val reference = FirebaseDatabase.getInstance().reference.child("users").child(registerAuth.uid!!)
            reference.addValueEventListener(object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    if(notify){
                        sendNotification(toId, user!!.username, textMessage)
                    }
                    notify = false
                }

            })
        }

    }

    private fun sendNotification(toId: String?, username: String?, textMessage: String) {
        val ref = FirebaseDatabase.getInstance().reference.child("tokens")

        val query = ref.orderByKey().equalTo(toId)
        query.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for(dataSnapshot in snapshot.children){
                    val token: Token? = dataSnapshot.getValue(Token::class.java)
                    val data = Data(registerAuth.uid!!, R.mipmap.ic_launcher, "$username: $textMessage", "Yeni Mesaj", toId!!)

                    val sender = Sender(data, token!!.getToken().toString())

                    apiService!!.sendNotification(sender)
                        .enqueue(object : Callback<MyResponse>{
                            override fun onFailure(call: Call<MyResponse>, t: Throwable) {
                                println(t.localizedMessage.toString())
                            }

                            override fun onResponse(
                                call: Call<MyResponse>,
                                response: Response<MyResponse>
                            ) {
                                if(response.code() == 200){
                                    if(response.body()!!.success !== 1){
                                        Toast.makeText(this@ChatActivity,"Failed", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }

                        })
                }
            }

        })
    }

}
