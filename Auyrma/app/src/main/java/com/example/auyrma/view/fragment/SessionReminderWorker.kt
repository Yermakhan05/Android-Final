package com.example.auyrma.view.fragment

import android.content.Context
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.auyrma.R


class SessionReminderWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val sessionTitle = inputData.getString("session_dr")
        val sessionTime = inputData.getString("session_time")

        showNotification(sessionTitle, sessionTime)

        return Result.success()
    }

    private fun showNotification(title: String?, time: String?) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationBuilder = NotificationCompat.Builder(applicationContext, "session_channel")
            .setContentTitle("Reminder: your session on $title")
            .setContentText("Scheduled at: $time")
            .setSmallIcon(R.drawable.ic_notifications)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "session_channel",
                "Session Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(1, notificationBuilder.build())
    }
}
