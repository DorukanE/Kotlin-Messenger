package com.dorukaneskiceri.kotlinmessenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_new_messages.*
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
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()

                snapshot.children.forEach {
                    val user = it.getValue(RegisterActivity.User::class.java)
                    if(user != null){
                        adapter.add(UserItem(user))
                    }
                    newMessageRecyclerView.adapter = adapter
                }
            }

        })
    }

    class UserItem(private val user: RegisterActivity.User): Item<GroupieViewHolder>(){
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
