package org.skynetsoftware.avnlauncher.data.database.model

import io.realm.kotlin.ext.realmSetOf
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.RealmSet
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlinx.datetime.Clock

class RealmGame : RealmObject {
    @PrimaryKey
    var f95ZoneThreadId: Int = -1
    var title: String = ""
    var imageUrl: String = ""
    var executablePath: String? = null
    var version: String = ""
    var playTime: Long = 0L
    var rating: Int = 0
    var f95Rating: Float = 0f
    var updateAvailable: Boolean = false
    var added: Long = Clock.System.now().toEpochMilliseconds()
    var lastPlayed: Long = 0L
    var lastUpdateCheck: Long = 0L
    var hidden: Boolean = false
    var releaseDate: Long = 0
    var firstReleaseDate: Long = 0
    var playState: String? = null
    var availableVersion: String? = null
    var tags: RealmSet<String> = realmSetOf()
    var lastRedirectUrl: String? = null
    var checkForUpdates: Boolean = true
}
