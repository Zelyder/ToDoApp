package com.zelyder.todoapp.presentation.core

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.zelyder.todoapp.R
import com.zelyder.todoapp.presentation.MainActivity

class Notifications {

    companion object {
        private const val CHANNEL_REMINDER = "ReminderChannel"
        const val REMINDER_TAG = "ReminderTag"
        private const val REQUEST_CODE = 1
    }

    private lateinit var notificationManager: NotificationManagerCompat

    fun initialize(context: Context) {
        notificationManager = NotificationManagerCompat.from(context)
        if (notificationManager.getNotificationChannel(CHANNEL_REMINDER) == null) {
            val notificationChannel = NotificationChannelCompat.Builder(
                CHANNEL_REMINDER,
                NotificationManagerCompat.IMPORTANCE_DEFAULT
            )
                .setName(context.resources.getString(R.string.reminder_channel_name))
                .setDescription(context.resources.getString(R.string.reminder_channel_description))
                .build()

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    fun show(context: Context, countTasksToDo: Int) {
        initialize(context)
        showNotification(context, countTasksToDo)
    }
    fun dismiss(countTasksToDo: Int) {
        notificationManager.cancel(REMINDER_TAG, countTasksToDo)
    }

    fun dismissAll() {
        notificationManager.cancelAll()
    }

    private fun showNotification(context: Context, countTasksToDo: Int) {

        val notification = NotificationCompat.Builder(context, CHANNEL_REMINDER)
            .setContentTitle(context.resources.getString(R.string.notification_text, countTasksToDo))
            .setSmallIcon(R.drawable.ic_info_outline)
            .setContentText(context.resources.getString(R.string.notification_description))
            .setContentIntent(
                PendingIntent.getActivity(
                    context,
                    REQUEST_CODE,
                    Intent(context, MainActivity::class.java)
                        .setAction(Intent.ACTION_VIEW),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            .build()

        notificationManager.notify(
            REMINDER_TAG,
            countTasksToDo,
            notification
        )
    }


}