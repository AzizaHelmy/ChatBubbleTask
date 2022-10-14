package com.aziza.chatbubbletask

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.aziza.chatbubbletask.databinding.ActivityMainBinding
import java.util.concurrent.Executor

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
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
            Handler(Looper.getMainLooper()).postDelayed({
                //Thread.sleep(5000L)
                val chat = chats.getValue(2L)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    notificationHelper.showNotification(chat,false)
                }

            },3000)
        }

    }
}