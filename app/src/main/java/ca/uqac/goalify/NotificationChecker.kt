package ca.uqac.goalify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class NotificationChecker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        Log.d("NotificationChecker", "Worker exécuté")

        try {
            // Récupère l'utilisateur connecté
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId == null) {
                Log.e("NotificationChecker", "Utilisateur non connecté")
                return Result.failure()
            }

            // Récupère les tâches de l'utilisateur depuis Firebase
            val database = FirebaseDatabase.getInstance()
            val tasksRef = database.getReference("users/$userId/tasks")
            val tasksSnapshot = tasksRef.get().await()

            // Obtenir le jour actuel de la semaine (e.g., "monday", "tuesday")
            val currentDay = SimpleDateFormat("EEEE", Locale.ENGLISH).format(Date()).lowercase()

            var taskFound = false

            // Parcours des tâches pour vérifier si une tâche est prévue aujourd'hui
            for (taskSnapshot in tasksSnapshot.children) {
                val days = taskSnapshot.child("days").child(currentDay).getValue(Boolean::class.java) ?: false
                if (days) {
                    val taskColor = taskSnapshot.child("color").getValue(String::class.java) ?: "Aucune couleur"
                    val taskType = taskSnapshot.child("name").getValue(String::class.java) ?: "Tâche inconnue"
                    sendNotification("Goalify", "Il est l'heure d'effectuer votre tâche: $taskType")
                    taskFound = true
                    break
                }
            }

            if (!taskFound) {
                Log.d("NotificationChecker", "Aucune tâche pour aujourd'hui.")
                // DEBUG: sendNotification("Aucune tâche pour aujourd'hui","Lancez Goalify pour commencer une streak!")
            }

        } catch (e: Exception) {
            Log.e("NotificationChecker", "Erreur lors de la vérification des tâches : ${e.message}")
            return Result.failure()
        }

        return Result.success()
    }

    private fun sendNotification(title: String, message: String) {
        val channelId = "firebase_task_channel"
        val channelName = "Firebase Task Channel"

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(1, notification)
    }
}
