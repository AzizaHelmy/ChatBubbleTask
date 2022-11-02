package com.aziza.chatbubbletask

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.BubbleMetadata
import androidx.core.app.Person
import androidx.core.net.toUri
import com.aziza.chatbubbletask.databinding.ActivityMainBinding
import java.util.concurrent.Executor

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val CHANNEL_ID = "new_messages"
    private val REQUEST_CONTENT = 1
    private val REQUEST_BUBBLE = 2

    private lateinit var executer: Executor
    private lateinit var notificationHelper: NotificationHelper
    private val chats = Contact.CONTACTS.map { contact ->
        contact.id to Chat(contact)
    }.toMap()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        notificationHelper = NotificationHelper(this)
        notificationHelper.setUpNotificationChannels()


        binding.send.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val chat = chats.getValue(2L)
                notificationHelper.showNotification(chat, false)
            }
//           // val icon = Icon.createWithAdaptiveBitmapContentUri("cat.jpg")
////            val user = android.app.Person.Builder().setName(context.getString(R.string.sender_you)).build()
////            val person = android.app.Person.Builder().setName(chat.contact.name).setIcon(icon).build()
//            val contentUri = "https://android.example.com/chat/${1}".toUri()
//
//            // Create bubble intent
//            val target = Intent(this, BubbleActivity::class.java)
//            val bubbleIntent = PendingIntent.getActivity(this, 0, target, 0 /* flags */)
//
//            // Create notification
//            val chatBot = Person.Builder()
//                .setBot(true)
//                .setName("BubbleBot")
//                .setImportant(true)
//                .build()
//             // Create bubble metadata
//            val bubbleData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                Notification.BubbleMetadata.Builder()
//                    .setDesiredHeight(600)
//                    // Note: although you can set the icon is not displayed in Q Beta 2
//                    .setIcon(Icon.createWithResource(this, R.drawable.remove))
//                    .setIntent(bubbleIntent)
//                    .build()
//            } else {
//                Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
//
//            }
//
//            val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                Notification.Builder(this, CHANNEL_ID)
//                    //.setContentIntent(contentIntent)
//                    .setSmallIcon(R.drawable.remove)
//                    .setBubbleMetadata(
//                        NotificationCompat.Builder( PendingIntent.getActivity(
//                            this,
//                            REQUEST_BUBBLE,
//                            Intent(this, BubbleActivity::class.java)
//                                .setAction(Intent.ACTION_VIEW)
//                                .setData(contentUri),
//                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//                        ),
//                            null)
//                       .setDesiredHeight(600)
//                        // Note: although you can set the icon is not displayed in Q Beta 2
//                        .setIcon(Icon.createWithResource(this, R.drawable.remove))
//                        .setIntent(bubbleIntent)
//                        .build())
//                    .addPerson( android.app.Person.Builder()
//                        .setName("contact.name")
//                        //.setIcon("icon")
//                        .build())
//            } else {
//                TODO("VERSION.SDK_INT < Q")
//            }
//            builder.build()
//
//        }
//
//    }
        }
    }
}