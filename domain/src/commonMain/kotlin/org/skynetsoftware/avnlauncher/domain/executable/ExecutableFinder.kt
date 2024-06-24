package org.skynetsoftware.avnlauncher.domain.executable

import org.skynetsoftware.avnlauncher.domain.model.Game

interface ExecutableFinder {
    fun validateExecutables(games: List<Game>): List<Pair<Int, Set<String>>>

    fun findExecutables(title: String): Set<String>
}
