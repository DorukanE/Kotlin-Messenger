package com.dorukaneskiceri.kotlinmessenger.models

class ChatMessage(val id: String, val message: String, val fromId: String, val toId: String, val timeStamp: Long){
    constructor() : this(id = "", message = "", fromId = "", toId = "", timeStamp = -1)
}