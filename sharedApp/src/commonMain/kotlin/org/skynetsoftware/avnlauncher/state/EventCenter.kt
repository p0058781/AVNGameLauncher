package org.skynetsoftware.avnlauncher.state

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.domain.model.Game

val eventCenterModule = module {
    single<EventCenter> { EventCenterImpl() }
}

interface EventCenter {
    val events: Flow<Event>

    fun emit(event: Event)
}

private class EventCenterImpl : EventCenter {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)

    private val _events = MutableSharedFlow<Event>(replay = 0)
    override val events: Flow<Event> get() = _events

    override fun emit(event: Event) {
        scope.launch {
            _events.emit(event)
        }
    }
}

sealed class Event {
    // UpdateChecker
    object UpdateCheckStarted : Event()

    object UpdateCheckComplete : Event()

    // GameLauncher
    class PlayingStarted(val game: Game) : Event()

    object PlayingEnded : Event()

    // SyncService
    object SyncStarted : Event()

    object SyncCompleted : Event()

    // Toast
    class ToastMessage<T>(val message: T, vararg val args: Any, val duration: Long = 3000L) : Event()
}
