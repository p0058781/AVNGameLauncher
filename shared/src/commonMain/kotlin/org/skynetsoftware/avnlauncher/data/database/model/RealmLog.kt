package org.skynetsoftware.avnlauncher.data.database.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.RealmUUID
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlinx.datetime.Clock
import org.skynetsoftware.avnlauncher.logging.Severity

class RealmLog : RealmObject {
    @PrimaryKey
    var id: RealmUUID = RealmUUID.random()
    var time: Long = Clock.System.now().toEpochMilliseconds()
    var severity: String = Severity.Info.name
    var logMessage: String = ""
}
