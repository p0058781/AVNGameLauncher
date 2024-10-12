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

@Suppress("InjectDispatcher")
class CheckForUpdatesWorker(
    context: Context,
    workerParameters: WorkerParameters,
    private val coroutineDispatchers: CoroutineDispatchers,
) : CoroutineWorker(context, workerParameters), KoinComponent {

    private val updateChecker by inject<UpdateChecker>()
    private val logger by inject<Logger>()

    override suspend fun doWork(): Result {
        logger.debug("doWork")
        return withContext(coroutineDispatchers.io) {
            updateChecker.checkForUpdates().join()
            Result.success()
        }
    }
}
