package com.dorukaneskiceri.kotlinmessenger.views

import com.dorukaneskiceri.kotlinmessenger.R
import com.dorukaneskiceri.kotlinmessenger.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.chat_screen_from.view.*
import kotlinx.android.synthetic.main.chat_screen_to.view.*

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