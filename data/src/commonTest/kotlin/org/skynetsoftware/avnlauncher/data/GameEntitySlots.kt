package org.skynetsoftware.avnlauncher.data

import io.mockk.CapturingSlot
import io.mockk.slot
import org.skynetsoftware.avnlauncher.domain.model.PlayState

class GameEntitySlots {
    val f95ZoneThreadId: CapturingSlot<Int> = slot()
    val title: CapturingSlot<String> = slot()
    val description: CapturingSlot<String> = slot()
    val developer: CapturingSlot<String> = slot()
    val imageUrl: CapturingSlot<String> = slot()
    val executablePaths: CapturingSlot<Set<String>> = slot()
    val version: CapturingSlot<String> = slot()
    val rating: CapturingSlot<Int> = slot()
    val f95Rating: CapturingSlot<Float> = slot()
    val updateAvailable: CapturingSlot<Boolean> = slot()
    val added: CapturingSlot<Long> = slot()
    val hidden: CapturingSlot<Boolean> = slot()
    val releaseDate: CapturingSlot<Long> = slot()
    val firstReleaseDate: CapturingSlot<Long> = slot()
    val playState: CapturingSlot<PlayState> = slot()
    val availableVersion: CapturingSlot<String?> = slot()
    val tags: CapturingSlot<Set<String>> = slot()
    val checkForUpdates: CapturingSlot<Boolean> = slot()
    val customImageUrl: CapturingSlot<String?> = slot()
    val notes: CapturingSlot<String?> = slot()
    val favorite: CapturingSlot<Boolean> = slot()
}
