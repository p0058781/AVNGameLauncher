package org.skynetsoftware.avnlauncher.launcher

/**
 * Delegate class to handle process with children
 * Currently handles waiting for and destroying the process and its children
 */
class ProcessWithChildren(private val process: Process) {

    val isAlive: Boolean
        get() = process.isAlive || process.children().anyMatch { it.isAlive }

    fun destroy() {
        process.children().forEach { it.destroy() }
        process.destroy()
    }

    fun waitFor(): Int {
        while (process.children().count() > 0) {
            Thread.sleep(500)
        }
        return process.waitFor()
    }

    fun exitValue(): Int {
        return process.exitValue()
    }
}