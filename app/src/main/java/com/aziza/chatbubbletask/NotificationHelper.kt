package com.aziza.chatbubbletask

import android.app.*
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
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.annotation.WorkerThread
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.net.toUri

object NotificationHelper {

    private const val CHANNEL_NEW_MESSAGES = "new_messages"
    private const val REQUEST_CONTENT = 1


    @RequiresApi(Build.VERSION_CODES.O)
    fun setUpNotificationChannels(context: Context) {
        Toast.makeText(context, "ye", Toast.LENGTH_SHORT).show()
//        val notificationManager: NotificationManager =
//            context.getSystemService() ?: throw IllegalStateException()
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (notificationManager.getNotificationChannel(CHANNEL_NEW_MESSAGES) == null) {
            notificationManager.createNotificationChannel(
                NotificationChannel(CHANNEL_NEW_MESSAGES, context.getString(R.string.channel_new_messages), NotificationManager.IMPORTANCE_HIGH).apply {
                    description = context.getString(R.string.channel_new_messages_description)
                }
            )
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun showNotification(context: Context,
                         @StringRes messageResId: Int,
                         @StringRes titleResId: Int,
                         notificationId: Int) {

        val title = context.getString(titleResId)
        val message = context.getString(messageResId)

        val notifyIntent = Intent(context, MainActivity::class.java)
        val bundle = Bundle()
        bundle.putString("T", title)
        bundle.putString("M", message)
        notifyIntent.putExtras(bundle)

        val pendingIntent = PendingIntent.getActivity(
            context, REQUEST_CONTENT, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(context, "1")
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.ic_message)
            .addAction(NotificationCompat.Action(null, "", pendingIntent))
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)

    }



    @RequiresApi(Build.VERSION_CODES.R)
    fun canBubble(context: Context): Boolean {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = notificationManager.getNotificationChannel(
            CHANNEL_NEW_MESSAGES,
           "1"
        )
        return notificationManager.areBubblesAllowed() || channel?.canBubble() == true
    }
}
