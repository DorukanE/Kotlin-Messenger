package com.dorukaneskiceri.kotlinmessenger.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.dorukaneskiceri.kotlinmessenger.R
import com.dorukaneskiceri.kotlinmessenger.models.User
import com.dorukaneskiceri.kotlinmessenger.registerlogin.RegisterActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_new_messages.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.new_message_user_row.*
import kotlinx.android.synthetic.main.new_message_user_row.view.*

class NewMessagesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_messages)

        supportActionBar?.title = "Select User"

        newMessageRecyclerView.layoutManager = LinearLayoutManager(this)

        fetchUsers()
    }

    // Fetching users from Firebase Database [ Firebase Firestore could be used. ]
    private fun fetchUsers(){

        val auth = FirebaseAuth.getInstance()
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()

                snapshot.children.forEach {
                    val user = it.getValue(User::class.java)
                    if(user != null && user.uid != auth.uid){
                        adapter.add(UserItem(user))
                    }
                }

                adapter.setOnItemClickListener { item, view ->

                    val userItem = item as UserItem
                    val intent = Intent(view.context, ChatActivity::class.java)
                    intent.putExtra("user",userItem.user)
                    startActivity(intent)
                    finish()
                }
                newMessageRecyclerView.adapter = adapter
            }
        })
    }

    class UserItem(val user: User): Item<GroupieViewHolder>(){
        override fun getLayout(): Int {
            return R.layout.new_message_user_row
        }

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            //it will implement for each user row
            viewHolder.itemView.usernameText.text = user.username
            Picasso.get().load(user.imageUrl).into(viewHolder.itemView.userProfileImage)
        }

    }
}
