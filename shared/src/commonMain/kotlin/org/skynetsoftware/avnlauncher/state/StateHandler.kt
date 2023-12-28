package org.skynetsoftware.avnlauncher.state

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.data.model.Game

val stateHandlerModule = module {
    single<StateHandler>(createdAtStart = true) { StateHandlerImpl(get()) }
}

interface StateHandler {
    val state: StateFlow<State>
}

private class StateHandlerImpl(private val eventCenter: EventCenter) : StateHandler {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
    private val _state = MutableStateFlow<State>(State.Idle)
    override val state: StateFlow<State> get() = _state

    private val activeStates = ActiveStates()

    private fun validateStates() {
        // TODO make sure ids are unique
    }

    init {
        validateStates()
        scope.launch {
            eventCenter.events.collect { event ->
                when (event) {
                    Event.PlayingEnded -> activeStates.removeAll { state -> state is State.Playing }
                    is Event.PlayingStarted -> activeStates.add(State.Playing(event.game))
                    Event.SyncCompleted -> activeStates.remove(State.Syncing)
                    Event.SyncStarted -> activeStates.add(State.Syncing)
                    Event.UpdateCheckComplete -> activeStates.remove(State.UpdateCheckRunning)
                    Event.UpdateCheckStarted -> activeStates.add(State.UpdateCheckRunning)
                }

                val currentState = activeStates.highestPriority
                _state.emit(currentState)
            }
        }
    }
}

class ActiveStates {
    private val activeStates = HashSet<State>()
    var highestPriority: State = State.Idle
        private set

    fun add(state: State) {
        activeStates.add(state)
        updateHighestPriority(state)
    }

    fun remove(state: State) {
        activeStates.remove(state)
        calculateHighestPriority()
    }

    fun removeAll(predicate: (State) -> Boolean) {
        activeStates.removeAll(predicate)
        calculateHighestPriority()
    }

    private fun calculateHighestPriority() {
        highestPriority = activeStates.minByOrNull { it.priority } ?: State.Idle
    }

    private fun updateHighestPriority(newState: State) {
        if (newState.priority < highestPriority.priority) {
            highestPriority = newState
        }
    }
}

sealed class State(val id: Int, val priority: Int) {
    object Idle : State(0, Int.MAX_VALUE)

    object UpdateCheckRunning : State(1, 0)

    class Playing(val game: Game) : State(2, 2)

    object Syncing : State(3, 1)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as State

        return id == other.id
    }

    override fun hashCode(): Int {
        return id
    }
}
