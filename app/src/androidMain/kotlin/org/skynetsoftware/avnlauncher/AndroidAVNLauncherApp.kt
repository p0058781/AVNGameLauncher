package org.skynetsoftware.avnlauncher

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.skynetsoftware.avnlauncher.app.generated.resources.Res
import org.skynetsoftware.avnlauncher.app.generated.resources.systemNotificationDescriptionUpdateAvailable
import org.skynetsoftware.avnlauncher.app.generated.resources.systemNotificationTitleUpdateAvailable
import org.skynetsoftware.avnlauncher.app.generated.resources.systemNotificationUpdatesChannelDescription
import org.skynetsoftware.avnlauncher.app.generated.resources.systemNotificationUpdatesChannelTitle
import org.skynetsoftware.avnlauncher.config.Config
import org.skynetsoftware.avnlauncher.data.dataKoinModule
import org.skynetsoftware.avnlauncher.domain.domainKoinModule
import org.skynetsoftware.avnlauncher.domain.repository.SettingsRepository
import org.skynetsoftware.avnlauncher.imageloader.imageLoaderKoinModule
import org.skynetsoftware.avnlauncher.launcher.gameLauncherKoinModule
import org.skynetsoftware.avnlauncher.link.externalLinkUtilsKoinModule
import org.skynetsoftware.avnlauncher.logger.Logger
import org.skynetsoftware.avnlauncher.logger.loggerKoinModule
import org.skynetsoftware.avnlauncher.server.httpServerKoinModule
import org.skynetsoftware.avnlauncher.state.Event
import org.skynetsoftware.avnlauncher.state.EventCenter
import org.skynetsoftware.avnlauncher.state.eventCenterModule
import org.skynetsoftware.avnlauncher.state.stateHandlerModule
import org.skynetsoftware.avnlauncher.ui.viewmodel.viewModelsKoinModule
import org.skynetsoftware.avnlauncher.updatechecker.updateCheckerKoinModule
import org.skynetsoftware.avnlauncher.utils.executableFinderKoinModule
import java.util.concurrent.TimeUnit

@Suppress("InjectDispatcher")
class AndroidAVNLauncherApp : Application(), KoinComponent {
    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "game_updates"
        private const val UPDATES_AVAILABLE_NOTIFICATION_ID = 1
    }

    private val notificationManager by lazy { getSystemService(NOTIFICATION_SERVICE) as NotificationManager }

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val settingsRepository by inject<SettingsRepository>()
    private val logger by inject<Logger>()
    private val eventCenter by inject<EventCenter>()

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        startKoin {
            androidContext(this@AndroidAVNLauncherApp)
            modules(
                imageLoaderKoinModule(),
                configKoinModule(
                    Config(
                        dataDir = dataDir.absolutePath,
                        cacheDir = cacheDir.absolutePath,
                    ),
                ),
                appKoinModule,
                dataKoinModule,
                domainKoinModule,
                loggerKoinModule,
                updateCheckerKoinModule,
                gameLauncherKoinModule,
                viewModelsKoinModule,
                eventCenterModule,
                stateHandlerModule,
                executableFinderKoinModule,
                externalLinkUtilsKoinModule,
                httpServerKoinModule,
            )
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
        coroutineScope.launch {
            eventCenter.events.collect {
                when (it) {
                    is Event.UpdateCheckComplete -> {
                        val count = it.updateCheckResult.updates.count { it.updateAvailable }
                        if (count > 0 && settingsRepository.systemNotificationsEnabled.value) {
                            sendNotification(count)
                        }
                    }
                    else -> {
                        // not handled here
                    }
                }
            }
        }
    }

    private suspend fun sendNotification(count: Int) {
        val builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_avn)
            .setContentTitle(org.jetbrains.compose.resources.getString(Res.string.systemNotificationTitleUpdateAvailable))
            .setContentText(
                org.jetbrains.compose.resources.getString(
                    Res.string.systemNotificationDescriptionUpdateAvailable,
                    count,
                ),
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager.notify(UPDATES_AVAILABLE_NOTIFICATION_ID, builder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CoroutineScope(Dispatchers.Main).launch {
                val name = org.jetbrains.compose.resources.getString(Res.string.systemNotificationUpdatesChannelTitle)
                val descriptionText =
                    org.jetbrains.compose.resources.getString(Res.string.systemNotificationUpdatesChannelDescription)
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
}
