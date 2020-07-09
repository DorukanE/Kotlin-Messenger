package com.dorukaneskiceri.kotlinmessenger.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(val uid: String, val username: String, val imageUrl: String): Parcelable {
    constructor(): this(uid = "", username = "", imageUrl = "")
}