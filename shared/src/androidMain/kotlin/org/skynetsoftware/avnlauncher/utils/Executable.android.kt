package org.skynetsoftware.avnlauncher.utils

import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.data.model.Game

actual val executableFinderKoinModule = module {
    single<ExecutableFinder> { ExecutableFinderAndroid() }
}

private class ExecutableFinderAndroid : ExecutableFinder {
    override fun validateExecutables(games: List<Game>): List<Pair<Game, String>> {
        // TODO not yet implemented
        return emptyList()
    }

    override fun findExecutable(title: String): String? {
        // TODO not yet implemented
        return null
    }
}
