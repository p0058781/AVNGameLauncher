package org.skynetsoftware.avnlauncher.sync

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.jakewharton.mosaic.runMosaicBlocking
import com.jakewharton.mosaic.ui.Column
import com.jakewharton.mosaic.ui.Text
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jline.terminal.TerminalBuilder
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.skynetsoftware.avnlauncher.data.dataKoinModule
import kotlin.system.exitProcess

fun main() {
    startKoin {
        modules(
            dataKoinModule,
            syncServiceModule,
            syncApiKoinModule,
        )
    }

    SyncMain().run()
}

private class SyncMain : KoinComponent {
    private val syncService by inject<SyncService>()

    fun run() =
        runMosaicBlocking {
            setContent {
                val syncState by remember { syncService.state }.collectAsState()
                Column {
                    Text(
                        value = when (syncState) {
                            SyncService.State.Stopped,
                            SyncService.State.Stopping,
                            -> "1. Start Sync (${syncState.name})"

                            SyncService.State.Idle,
                            SyncService.State.Syncing,
                            -> "1. Stop Sync (${syncState.name})"
                        },
                    )
                    Text(
                        value = "q. Quit",
                    )
                }
            }

            withContext(Dispatchers.IO) {
                val terminal = TerminalBuilder.terminal()
                terminal.enterRawMode()
                val reader = terminal.reader()

                while (true) {
                    when (reader.read()) {
                        'q'.code -> exitProcess(0)
                        '1'.code -> when (syncService.state.value) {
                            SyncService.State.Stopped,
                            SyncService.State.Stopping,
                            -> syncService.start()

                            SyncService.State.Idle,
                            SyncService.State.Syncing,
                            -> syncService.stop()
                        }
                    }
                }
            }
        }
}
