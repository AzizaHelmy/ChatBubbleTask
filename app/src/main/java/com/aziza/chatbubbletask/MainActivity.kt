package com.aziza.chatbubbletask

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.content.LocusIdCompat
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.net.toUri
import com.aziza.chatbubbletask.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val CHANNEL_NEW_MESSAGES = "new_messages"
    private val CHANNEL_ID = "1"
    private val REQUEST_CODE = 0
    private val _messages = mutableListOf(
        Message(1L, 1, "Send me a message", null, null, System.currentTimeMillis()),
        Message(2L, 2, "I will reply in 5 seconds", null, null, System.currentTimeMillis())
    )
    val messages: List<Message>
        get() = _messages

    data class Message(
        val id: Long,
        val sender: Long,
        val text: String,
        val photoUri: Uri?,
        val photoMimeType: String?,
        val timestamp: Long
    )

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // binding.textView.text =
        createNotificationChannel()

        binding.send.setOnClickListener {

            Handler().postDelayed({
                setUpNotification(true)
            }, 3000)

        }


    }

    @WorkerThread
    fun updateShortcuts(importantContact: Contact?) {
        var shortcuts = Contact.CONTACTS.map { contact ->
            val icon = IconCompat.createWithAdaptiveBitmap(
                this.resources.assets.open(contact.icon).use { input ->
                    BitmapFactory.decodeStream(input)
                }
            )

            // Create a dynamic shortcut for each of the contacts.
            // The same shortcut ID will be used when we show a bubble notification.
            ShortcutInfoCompat.Builder(this, contact.shortcutId)
                .setLocusId(LocusIdCompat(contact.shortcutId))
                .setActivity(ComponentName(this, MainActivity::class.java))
                .setShortLabel(contact.name)
                .setIcon(icon)
                .setLongLived(true)
                .setCategories(setOf("com.example.android.bubbles.category.TEXT_SHARE_TARGET"))
                .setIntent(
                    Intent(this, MainActivity::class.java)
                        .setAction(Intent.ACTION_VIEW)
                        .setData(
                            Uri.parse(
                                "https://android.example.com/chat/${contact.id}"
                            )
                        )
                )
                .setPerson(
                    Person.Builder()
                        .setName(contact.name)
                        .setIcon(icon)
                        .build()
                )
                .build()
        }
        // Move the important contact to the front of the shortcut list.
        if (importantContact != null) {
            shortcuts = shortcuts.sortedByDescending { it.id == importantContact.shortcutId }
        }
        // Truncate the list if we can't show all of our contacts.
        val maxCount = ShortcutManagerCompat.getMaxShortcutCountPerActivity(this)
        if (shortcuts.size > maxCount) {
            shortcuts = shortcuts.take(maxCount)
        }
        for (shortcut in shortcuts) {
            ShortcutManagerCompat.pushDynamicShortcut(this, shortcut)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun setUpNotification(b: Boolean) {
        val contentUri = "https://android.example.com/chat/1".toUri()

        val notifyIntent = Intent(this, BubbleActivity::class.java)
            .setAction(Intent.ACTION_VIEW)
            .setData(contentUri)
        val pendingIntent = PendingIntent.getActivity(
            this,
            REQUEST_CODE,
            notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
        val icon = IconCompat.createWithAdaptiveBitmapContentUri("cat.jpg")
        val user = Person.Builder().setName("you").build()
        val person = Person.Builder().setName("Null").setIcon(icon).build()

        val messagingStyle = NotificationCompat.MessagingStyle(user)

        var builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_delete)
            .setContentTitle("textTitle")
            .setContentText("textContent")
            .setStyle(messagingStyle)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(Notification.CATEGORY_MESSAGE)
            .setShortcutId(1.toString())
            .setLocusId(LocusIdCompat(1.toString()))
            .addPerson(person)
            .setShowWhen(true)
            .setContentIntent(
                pendingIntent
            )
            // Direct Reply
            .addAction(NotificationCompat.Action(null, "Open Activity", pendingIntent))
            .setBubbleMetadata(
                NotificationCompat.BubbleMetadata
                    .Builder(pendingIntent, icon)
                    .setDesiredHeight(300)
                    // .setSuppressNotification(false)
                    .setAutoExpandBubble(true)
                    .setDesiredHeightResId(R.dimen.app_icon_size)
                    .build()
            )


        with(NotificationManagerCompat.from(this)) {
            getNotificationChannel(CHANNEL_ID)?.setAllowBubbles(true)
            notify(1, builder.build())
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.cancel)
            val descriptionText = getString(R.string.dialog_alert_title)
            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            // notificationManager.getNotificationChannel(CHANNEL_ID).setAllowBubbles(true)
            updateShortcuts(null)
        }
    }

}