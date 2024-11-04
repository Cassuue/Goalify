package ca.uqac.goalify

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import java.time.Duration

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val workRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                PeriodicWorkRequestBuilder<NotificationChecker>(
                    Duration.ofMinutes(1)
                ).build()
            } else {
                TODO("VERSION.SDK_INT < O")
            }

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "MyPeriodicWorker",
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest
            )
        }
    }
}
