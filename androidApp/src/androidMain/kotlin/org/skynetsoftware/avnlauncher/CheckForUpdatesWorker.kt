package org.skynetsoftware.avnlauncher

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.skynetsoftware.avnlauncher.logger.Logger
import org.skynetsoftware.avnlauncher.updatechecker.UpdateChecker

@Suppress("InjectDispatcher")
class CheckForUpdatesWorker(private val context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters),
    KoinComponent {
    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "game_updates"
        private const val UPDATES_AVAILABLE_NOTIFICATION_ID = 1
    }

    private val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = MR.strings.systemNotificationUpdatesChannelTitle.getString(context)
            val descriptionText = MR.strings.systemNotificationUpdatesChannelDescription.getString(context)
            val mChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT)
            mChannel.description = descriptionText
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    private val updateChecker by inject<UpdateChecker>()
    private val logger by inject<Logger>()

    override suspend fun doWork(): Result {
        logger.debug("doWork")
        return withContext(Dispatchers.IO) {
            val result = updateChecker.checkForUpdates(this, true)
            val count = result.updates.count { it.updateAvailable }
            if (count > 0) {
                sendNotification(count)
            }
            Result.success()
        }
    }

    private fun sendNotification(count: Int) {
        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_avn)
            .setContentTitle(MR.strings.systemNotificationTitleUpdateAvailable.getString(context))
            .setContentText(
                context.getString(MR.strings.systemNotificationDescriptionUpdateAvailable.resourceId, count),
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager.notify(UPDATES_AVAILABLE_NOTIFICATION_ID, builder.build())
    }
}
