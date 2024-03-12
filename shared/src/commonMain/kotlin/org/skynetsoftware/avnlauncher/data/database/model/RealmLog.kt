package org.skynetsoftware.avnlauncher.data.database.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.RealmUUID
import io.realm.kotlin.types.annotations.PrimaryKey
import org.skynetsoftware.avnlauncher.logging.Severity

class RealmLog(
    @PrimaryKey
    val id: RealmUUID = RealmUUID.random(),
    val time: Long,
    val severity: Severity,
    val logMessage: String
) : RealmObject
