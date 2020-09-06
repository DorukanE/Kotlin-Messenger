package com.dorukaneskiceri.kotlinmessenger.notifications

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationBuilderWithBuilderAccessor
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import com.dorukaneskiceri.kotlinmessenger.messages.ChatActivity
import com.dorukaneskiceri.kotlinmessenger.messages.LatestMessagesActivity
import com.dorukaneskiceri.kotlinmessenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessaging: FirebaseMessagingService() {

    override fun onMessageReceived(mRemoteMessage: RemoteMessage) {
        super.onMessageReceived(mRemoteMessage)

        val sent = mRemoteMessage.data["sent"]
        val user = mRemoteMessage.data["user"]

        val sharedPreferences = getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        val currentOnlineUser = sharedPreferences.getString("currentUser","none")

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if(firebaseUser != null && sent == firebaseUser.uid){
            if(currentOnlineUser != user){
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    sendOreoNotification(mRemoteMessage)
                }
                else{
                    sendNotification(mRemoteMessage)
                }
            }
        }
    }

    private fun sendOreoNotification(mRemoteMessage: RemoteMessage){
        val user = mRemoteMessage.data["user"]
        val icon = mRemoteMessage.data["icon"]
        val title = mRemoteMessage.data["title"]
        val body = mRemoteMessage.data["body"]

        val counter = user!!.replace("[\\D]".toRegex(), "").toInt()
        val intent = Intent(this, LatestMessagesActivity::class.java)

        val pendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val oreoNotification = OreoNotification(this)
        val builder: Notification.Builder = oreoNotification.getOreoNotification(title, body, pendingIntent, defaultSound, icon)

        var i = 0
        if(counter > 0){
            i = counter
        }

        oreoNotification.getManager!!.notify(i, builder.build())
    }

    private fun sendNotification(mRemoteMessage: RemoteMessage){

        val user = mRemoteMessage.data["user"]
        val icon = mRemoteMessage.data["icon"]
        val title = mRemoteMessage.data["title"]
        val body = mRemoteMessage.data["body"]

        val counter = user!!.replace("[\\D]".toRegex(), "").toInt()
        val intent = Intent(this, LatestMessagesActivity::class.java)

        val pendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this).apply {
            setLights(Color.rgb(0,0,255),1500,1000)
            setSmallIcon(icon!!.toInt())
            setContentTitle(title)
            setContentText(body)
            setAutoCancel(true)
            setSound(defaultSound)
            setContentIntent(pendingIntent)
        }
//        with(NotificationManagerCompat.from(this)){
//            notify(counter,builder.build())
//        }

        val noti = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        var i = 0
        if(counter > 0){
            i = counter
        }

        noti.notify(i, builder.build())
    }
}