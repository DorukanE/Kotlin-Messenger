package com.dorukaneskiceri.kotlinmessenger.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.dorukaneskiceri.kotlinmessenger.R
import com.dorukaneskiceri.kotlinmessenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.chat_screen_from.view.*
import kotlinx.android.synthetic.main.chat_screen_to.view.*
import kotlinx.android.synthetic.main.chat_screen_to.view.circleImageView

class ChatActivity : AppCompatActivity() {

    private val registerAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val userKey = intent.getParcelableExtra<User>("user")
        supportActionBar?.title = userKey.username

        recyclerview_chat_screen.layoutManager = LinearLayoutManager(this)
        val adapter = GroupAdapter<GroupieViewHolder>()
        recyclerview_chat_screen.adapter = adapter

        adapter.add(ChatItemFrom(userKey))
        adapter.add(ChatItemTo(userKey))
        adapter.add(ChatItemFrom(userKey))
        adapter.add(ChatItemTo(userKey))
        adapter.add(ChatItemFrom(userKey))
        adapter.add(ChatItemTo(userKey))
        adapter.add(ChatItemFrom(userKey))
        adapter.add(ChatItemTo(userKey))
        adapter.add(ChatItemFrom(userKey))
        adapter.add(ChatItemTo(userKey))

        sendButton.setOnClickListener {

        }
    }

    class ChatItemFrom(val userKey: User): Item<GroupieViewHolder>(){
        override fun getLayout(): Int {
            return R.layout.chat_screen_from
        }

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {

        }
    }

    class ChatItemTo(val userUrl: User): Item<GroupieViewHolder>(){
        override fun getLayout(): Int {
            return R.layout.chat_screen_to
        }

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {

        }
    }

}
