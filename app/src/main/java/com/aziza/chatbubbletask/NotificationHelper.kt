

package com.aziza.chatbubbletask

import android.app.Notification
import android.app.NotificationChannel

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Person
import android.app.RemoteInput
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.LocusId
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import androidx.core.content.getSystemService
import androidx.core.net.toUri


/**
 * Handles all operations related to [Notification].
 */
class NotificationHelper(private val context: Context) {

    companion object {
        /**
         * The notification channel for messages. This is used for showing Bubbles.
         */
        private const val CHANNEL_NEW_MESSAGES = "new_messages"

        private const val REQUEST_CONTENT = 1
        private const val REQUEST_BUBBLE = 2
    }

    private val notificationManager: NotificationManager =
        context.getSystemService() ?: throw IllegalStateException()

    private val shortcutManager: ShortcutManager =
        context.getSystemService() ?: throw IllegalStateException()

    @RequiresApi(Build.VERSION_CODES.O)
    fun setUpNotificationChannels() {
        if (notificationManager.getNotificationChannel(CHANNEL_NEW_MESSAGES) == null) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_NEW_MESSAGES,
                    context.getString(R.string.channel_new_messages),
                    // The importance must be IMPORTANCE_HIGH to show Bubbles.
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = context.getString(R.string.channel_new_messages_description)
                }
            )
        }
        updateShortcuts(null)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @WorkerThread
    fun updateShortcuts(importantContact: Contact?) {
        // TODO 2: Create dynamic shortcuts.
        var shortcuts = Contact.CONTACTS.map { contact ->
            val icon = Icon.createWithAdaptiveBitmap(
                context.resources.assets.open(contact.icon).use { input ->
                    BitmapFactory.decodeStream(input)
                }
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ShortcutInfo.Builder(context, contact.shortcutId)
                    .setLocusId(LocusId(contact.shortcutId))
                    .setActivity(ComponentName(context, MainActivity::class.java))
                    .setShortLabel(contact.name)
                    .setIcon(icon)
                    .setLongLived(true)
                    .setCategories(setOf("com.example.android.bubbles.category.TEXT_SHARE_TARGET"))
                    .setIntent(
                        Intent(context, MainActivity::class.java)
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
            } else {
                TODO("VERSION.SDK_INT < Q")
            }
        }
        if (importantContact != null) {
            shortcuts = shortcuts.sortedByDescending { it.id == importantContact.shortcutId }
        }
        val maxCount = shortcutManager.maxShortcutCountPerActivity
        if (shortcuts.size > maxCount) {
            shortcuts = shortcuts.take(maxCount)
        }
        shortcutManager.addDynamicShortcuts(shortcuts)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    @WorkerThread
    fun showNotification(chat: Chat, fromUser: Boolean) {
        updateShortcuts(chat.contact)
        val icon = Icon.createWithAdaptiveBitmapContentUri(chat.contact.iconUri)
        val user = Person.Builder().setName(context.getString(R.string.sender_you)).build()
        val person = Person.Builder().setName(chat.contact.name).setIcon(icon).build()
        val contentUri = "https://android.example.com/chat/${chat.contact.id}".toUri()

        val builder = Notification.Builder(context, CHANNEL_NEW_MESSAGES)
            // TODO 5: Set up a BubbleMetadata.
            .setBubbleMetadata(
                Notification.BubbleMetadata
                    .Builder(
                        PendingIntent.getActivity(
                            context,
                            REQUEST_BUBBLE,
                            Intent(context, BubbleActivity::class.java)
                                .setAction(Intent.ACTION_VIEW)
                                .setData(contentUri),
                            PendingIntent.FLAG_UPDATE_CURRENT
                        ),
                        icon
                    )
                    .setDesiredHeightResId(R.dimen.bubble_height)
                    .apply {
                        if (fromUser) {
                            setAutoExpandBubble(true)
                            setSuppressNotification(true)
                        }
                    }
                    .build()
            )
            // The user can turn off the bubble in system settings. In that case, this notification
            // is shown as a normal notification instead of a bubble. Make sure that this
            // notification works as a normal notification as well.
            .setContentTitle(chat.contact.name)
            .setSmallIcon(R.drawable.ic_message)
            // TODO 4: Associate the notification with a shortcut.
            .setCategory(Notification.CATEGORY_MESSAGE)
            .setShortcutId(chat.contact.shortcutId)
            .setLocusId(LocusId(chat.contact.shortcutId))
            .addPerson(person)
            .setShowWhen(true)
            // The content Intent is used when the user clicks on the "Open Content" icon button on
            // the expanded bubble, as well as when the fall-back notification is clicked.
//            .setContentIntent(
//                PendingIntent.getActivity(
//                    context,
//                    REQUEST_CONTENT,
//                    Intent(context, MainActivity::class.java)
//                        .setAction(Intent.ACTION_VIEW)
//                        .setData(contentUri),
//                    PendingIntent.FLAG_UPDATE_CURRENT
//                )
//            )
            // Direct Reply
//            .addAction(
//                Notification.Action
//                    .Builder(
//                        Icon.createWithResource(context, R.drawable.ic_send),
//                        context.getString(R.string.label_reply),
//                        PendingIntent.getBroadcast(
//                            context,
//                            REQUEST_CONTENT,
//                            Intent(context, ReplyReceiver::class.java).setData(contentUri),
//                            PendingIntent.FLAG_UPDATE_CURRENT
//                        )
//                    )
//                    .addRemoteInput(
//                        RemoteInput.Builder(ReplyReceiver.KEY_TEXT_REPLY)
//                            .setLabel(context.getString(R.string.hint_input))
//                            .build()
//                    )
//                    .setAllowGeneratedReplies(true)
//                    .build()
//            )
            // TODO 1: Use MessagingStyle.
            .setStyle(
                Notification.MessagingStyle(user)
                    .apply {
                        val lastId = chat.messages.last().id
                        for (message in chat.messages) {
                            val m = Notification.MessagingStyle.Message(
                                message.text,
                                message.timestamp,
                                if (message.isIncoming) person else null
                            ).apply {
                                if (message.photoUri != null) {
                                    setData(message.photoMimeType, message.photoUri)
                                }
                            }
                            if (message.id < lastId) {
                                addHistoricMessage(m)
                            } else {
                                addMessage(m)
                            }
                        }
                    }
                    .setGroupConversation(false)
            )
            .setWhen(chat.messages.last().timestamp)
            .setContentText(chat.messages.last().text)
            .setWhen(chat.messages.last().timestamp)

        notificationManager.notify(chat.contact.id.toInt(), builder.build())
    }

    fun dismissNotification(id: Long) {
        notificationManager.cancel(id.toInt())
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun canBubble(contact: Contact): Boolean {
        val channel = notificationManager.getNotificationChannel(
            CHANNEL_NEW_MESSAGES,
            contact.shortcutId
        )
        return notificationManager.areBubblesAllowed() || channel?.canBubble() == true
    }
}
