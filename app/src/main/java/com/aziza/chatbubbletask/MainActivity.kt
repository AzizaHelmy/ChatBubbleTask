package com.aziza.chatbubbletask

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.aziza.chatbubbletask.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val NOTIFICATION_ID = 77



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationHelper.setUpNotificationChannels(this)
        }


        binding.send.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationHelper.showNotification(this,R.string.channel_new_messages,R.string.channel_new_messages_description,NOTIFICATION_ID)
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