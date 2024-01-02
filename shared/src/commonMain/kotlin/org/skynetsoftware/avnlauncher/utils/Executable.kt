package org.skynetsoftware.avnlauncher.utils

import org.koin.core.module.Module
import org.skynetsoftware.avnlauncher.data.model.Game

expect val executableFinderKoinModule: Module

interface ExecutableFinder {
    fun validateExecutables(games: List<Game>): List<Pair<Int, Set<String>>>

    fun findExecutables(title: String): Set<String>
}
