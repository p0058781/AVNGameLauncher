package org.skynetsoftware.avnlauncher

import android.app.Application
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.skynetsoftware.avnlauncher.config.Config
import org.skynetsoftware.avnlauncher.domain.repository.SettingsRepository
import org.skynetsoftware.avnlauncher.logger.Logger
import java.util.concurrent.TimeUnit

@Suppress("InjectDispatcher")
class AndroidAVNLauncherApp : Application(), KoinComponent {
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val settingsRepository by inject<SettingsRepository>()
    private val logger by inject<Logger>()

    override fun onCreate() {
        super.onCreate()
        AVNLauncherApp.onCreate(
            config = Config(
                dataDir = dataDir.absolutePath,
                cacheDir = cacheDir.absolutePath,
            ),
        ) {
            androidContext(this@AndroidAVNLauncherApp)
        }

        coroutineScope.launch {
            settingsRepository.periodicUpdateChecksEnabled.collect { periodicUpdateChecksEnabled ->
                logger.debug("periodicUpdateChecksEnabled: $periodicUpdateChecksEnabled")
                if (periodicUpdateChecksEnabled) {
                    val request =
                        PeriodicWorkRequestBuilder<CheckForUpdatesWorker>(1, TimeUnit.HOURS)
                            .build()
                    WorkManager.getInstance(this@AndroidAVNLauncherApp).enqueue(request)
                } else {
                    WorkManager.getInstance(this@AndroidAVNLauncherApp).cancelAllWork()
                }
            }
        }
    }
}
