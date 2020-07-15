package com.dorukaneskiceri.kotlinmessenger.messages

import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Sampler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.dorukaneskiceri.kotlinmessenger.R
import com.dorukaneskiceri.kotlinmessenger.models.ChatMessage
import com.dorukaneskiceri.kotlinmessenger.models.User
import com.dorukaneskiceri.kotlinmessenger.registerlogin.RegisterActivity
import com.dorukaneskiceri.kotlinmessenger.views.LatestChatScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_latest_messages.*
import kotlinx.android.synthetic.main.latest_chat_screen.view.*

class LatestMessagesActivity : AppCompatActivity() {

    private val adapter = GroupAdapter<GroupieViewHolder>()

    companion object{
        var currentUser: User? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)


        recyclerView_latest_messages.layoutManager = LinearLayoutManager(this)
        recyclerView_latest_messages.adapter = adapter

        listenForLatestMessages()

        fetchCurrentUsers()

        checkUserIsLoggedIn()

        adapter.setOnItemClickListener { item, view ->
            val intent = Intent(view.context,ChatActivity::class.java)
            val user = item as LatestChatScreen
            intent.putExtra("user", user.user)
            startActivity(intent)
        }
    }

    //Used hashmap because we didn't update inside of the recyclerView
    val latestMessagesMap = HashMap<String, ChatMessage>()

    private fun refreshRecyclerViewMessages(){
        adapter.clear()
        latestMessagesMap.values.forEach {
            adapter.add(LatestChatScreen(it))
        }
    }

    private fun listenForLatestMessages(){
        val fromId = FirebaseAuth.getInstance().uid
        val reference = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")

        reference.addChildEventListener(object : ChildEventListener{

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val latestMessage = snapshot.getValue(ChatMessage::class.java)
                if(latestMessage != null){
                    latestMessagesMap[snapshot.key!!] = latestMessage
                    refreshRecyclerViewMessages()
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val updateMessage = snapshot.getValue(ChatMessage::class.java)
                if(updateMessage != null){
                    latestMessagesMap[snapshot.key!!] = updateMessage
                    refreshRecyclerViewMessages()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}

        })
    }


    private fun fetchCurrentUsers(){
        val uid = FirebaseAuth.getInstance().uid
        val database = FirebaseDatabase.getInstance().getReference("/users/$uid")

        database.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(User::class.java)
            }

        })
    }

    private fun checkUserIsLoggedIn(){
        val uid = FirebaseAuth.getInstance().uid

        if(uid == null){
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.navbar_options_menu,menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.newMessage -> {
                val intent = Intent(this,
                    NewMessagesActivity::class.java)
                startActivity(intent)
            }
            R.id.signOut -> {
                FirebaseAuth.getInstance().signOut()
                Toast.makeText(this,"Signing Out..",Toast.LENGTH_SHORT).show()
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
