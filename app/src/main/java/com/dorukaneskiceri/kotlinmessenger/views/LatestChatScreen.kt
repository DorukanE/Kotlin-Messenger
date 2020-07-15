package com.dorukaneskiceri.kotlinmessenger.views

import com.dorukaneskiceri.kotlinmessenger.R
import com.dorukaneskiceri.kotlinmessenger.models.ChatMessage
import com.dorukaneskiceri.kotlinmessenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.latest_chat_screen.view.*

class LatestChatScreen(val message: ChatMessage): Item<GroupieViewHolder>(){

    var user: User? = null

    override fun getLayout(): Int {
        return R.layout.latest_chat_screen
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textLatestMessagesMessage.text = message.message

        val partnerId: String
        if(message.fromId == FirebaseAuth.getInstance().uid){
            partnerId = message.toId
        }else{
            partnerId = message.fromId
        }

        val ref = FirebaseDatabase.getInstance().getReference("/users/$partnerId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {println(error.message)}

            override fun onDataChange(snapshot: DataSnapshot) {
                user = snapshot.getValue(User::class.java)
                viewHolder.itemView.textLatestMessagesUsername.text = user?.username
                Picasso.get().load(user?.imageUrl).into(viewHolder.itemView.imageView_latest_messages)
            }

        })
    }

}