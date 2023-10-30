package org.skynetsoftware.avnlauncher.data.database.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlinx.datetime.Clock

class RealmGame : RealmObject {
    @PrimaryKey
    var title: String = ""
    var imageUrl: String = ""
    var f95ZoneUrl: String = ""
    var executablePath: String? = null
    var version: String = ""
    var playTime: Long = 0L
    var rating: Int = 0
    var updateAvailable: Boolean = false
    var added: Long = Clock.System.now().toEpochMilliseconds()
    var lastPlayed: Long = 0L
    var lastUpdateCheck: Long = 0L
    var hidden: Boolean = false
    var releaseDate: String? = null
    var playState: String? = null
    var availableVersion: String? = null
}
