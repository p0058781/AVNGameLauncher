package org.skynetsoftware.avnlauncher.state

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.domain.model.Game
import org.skynetsoftware.avnlauncher.updatechecker.UpdateCheckResult
import org.skynetsoftware.avnlauncher.updatechecker.UpdateResult

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

    class UpdateCheckComplete(val updateCheckResult: UpdateCheckResult) : Event()

    object UpdateSeen : Event()

    object UpdatingGamesStarted : Event()

    class UpdatingGamesComplete(val updateResult: UpdateResult) : Event()

    // GameLauncher
    class PlayingStarted(val game: Game) : Event()

    object PlayingEnded : Event()

    // Toast
    class ToastMessage<T>(val message: T, vararg val args: Any, val duration: Long = 3000L) : Event()
}
