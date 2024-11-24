package org.skynetsoftware.avnlauncher

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.skynetsoftware.avnlauncher.domain.coroutines.CoroutineDispatchers
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
