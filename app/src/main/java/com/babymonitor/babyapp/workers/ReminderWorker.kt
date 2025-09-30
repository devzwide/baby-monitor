package com.babymonitor.babyapp.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.babymonitor.babyapp.utils.NotificationHelper

class ReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {
    override fun doWork(): Result {
        val title = inputData.getString("title") ?: "Baby Monitor Reminder"
        val message = inputData.getString("message") ?: "It's time to check on your baby!"
        NotificationHelper.showNotification(applicationContext, title, message)
        return Result.success()
    }
}

