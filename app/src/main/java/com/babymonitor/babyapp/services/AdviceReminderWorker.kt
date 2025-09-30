package com.babymonitor.babyapp.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.babymonitor.babyapp.MainActivity
import com.babymonitor.babyapp.R

class AdviceReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {
    private val adviceList = listOf(
        "Feeding: Remember to feed your baby every 2-3 hours for healthy growth.",
        "Sleeping: Ensure your baby gets enough sleep and monitor nap patterns.",
        "Diapers: Check and change diapers regularly to prevent rashes.",
        "Health: Track temperature and watch for any signs of illness."
    )

    override fun doWork(): Result {
        val adviceIndex = (System.currentTimeMillis() / 60000 % adviceList.size).toInt()
        val advice = adviceList[adviceIndex]
        sendNotification("Baby Care Advice", advice)
        return Result.success()
    }

    private fun sendNotification(title: String, message: String) {
        val channelId = "baby_monitor_channel"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Baby Monitor Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        notificationManager.notify((System.currentTimeMillis() / 1000).toInt(), notificationBuilder.build())
    }
}

