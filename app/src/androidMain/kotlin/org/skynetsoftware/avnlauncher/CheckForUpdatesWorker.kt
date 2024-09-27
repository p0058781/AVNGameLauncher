package org.skynetsoftware.avnlauncher

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.getString
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.skynetsoftware.avnlauncher.app.generated.resources.Res
import org.skynetsoftware.avnlauncher.app.generated.resources.systemNotificationDescriptionUpdateAvailable
import org.skynetsoftware.avnlauncher.app.generated.resources.systemNotificationTitleUpdateAvailable
import org.skynetsoftware.avnlauncher.app.generated.resources.systemNotificationUpdatesChannelDescription
import org.skynetsoftware.avnlauncher.app.generated.resources.systemNotificationUpdatesChannelTitle
import org.skynetsoftware.avnlauncher.domain.coroutines.CoroutineDispatchers
import org.skynetsoftware.avnlauncher.domain.repository.SettingsRepository
import org.skynetsoftware.avnlauncher.logger.Logger
import org.skynetsoftware.avnlauncher.updatechecker.UpdateChecker

@OptIn(ExperimentalResourceApi::class)
@Suppress("InjectDispatcher")
class CheckForUpdatesWorker(
    private val context: Context,
    workerParameters: WorkerParameters,
    private val coroutineDispatchers: CoroutineDispatchers,
) :
    CoroutineWorker(context, workerParameters),
        KoinComponent {
    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "game_updates"
        private const val UPDATES_AVAILABLE_NOTIFICATION_ID = 1
    }

    private val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CoroutineScope(coroutineDispatchers.main).launch {
                val name = getString(Res.string.systemNotificationUpdatesChannelTitle)
                val descriptionText = getString(Res.string.systemNotificationUpdatesChannelDescription)
                val mChannel = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    name,
                    NotificationManager.IMPORTANCE_DEFAULT,
                )
                mChannel.description = descriptionText
                notificationManager.createNotificationChannel(mChannel)
            }
        }
    }

    private val updateChecker by inject<UpdateChecker>()
    private val logger by inject<Logger>()
    private val settingsRepository by inject<SettingsRepository>()

    override suspend fun doWork(): Result {
        logger.debug("doWork")
        return withContext(coroutineDispatchers.io) {
            val result = updateChecker.checkForUpdates(this)
            val count = result.updates.count { it.updateAvailable }
            if (count > 0 && settingsRepository.systemNotificationsEnabled.value) {
                sendNotification(count)
            }
            Result.success()
        }
    }

    @OptIn(ExperimentalResourceApi::class)
    private suspend fun sendNotification(count: Int) {
        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_avn)
            .setContentTitle(getString(Res.string.systemNotificationTitleUpdateAvailable))
            .setContentText(getString(Res.string.systemNotificationDescriptionUpdateAvailable, count))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager.notify(UPDATES_AVAILABLE_NOTIFICATION_ID, builder.build())
    }
}
