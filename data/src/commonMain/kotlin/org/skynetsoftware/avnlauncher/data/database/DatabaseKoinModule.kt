@file:Suppress("MatchingDeclarationName")

package org.skynetsoftware.avnlauncher.data.database

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.adapter.primitive.FloatColumnAdapter
import app.cash.sqldelight.adapter.primitive.IntColumnAdapter
import org.jetbrains.annotations.VisibleForTesting
import org.koin.core.module.Module
import org.skynetsoftware.avnlauncher.data.Database
import org.skynetsoftware.avnlauncher.data.GameEntity
import org.skynetsoftware.avnlauncher.data.PlaySessionEntity

@VisibleForTesting
internal object StringSetAdapter : ColumnAdapter<Set<String>, String> {
    override fun decode(databaseValue: String) =
        if (databaseValue.isBlank()) {
            setOf()
        } else {
            databaseValue.split(",").toMutableList().apply { removeAll { it.isBlank() } }.map { it.trim() }.toSet()
        }

    override fun encode(value: Set<String>) =
        value.toMutableSet().apply { removeAll { it.isBlank() } }.joinToString(separator = ",") { it.trim() }
}

internal fun Module.databaseKoinModule() {
    single<Database> {
        val driver = get<DriverFactory>().createDriver()
        Database(
            driver = driver,
            GameEntityAdapter = GameEntity.Adapter(
                f95ZoneThreadIdAdapter = IntColumnAdapter,
                ratingAdapter = IntColumnAdapter,
                f95RatingAdapter = FloatColumnAdapter,
                tagsAdapter = StringSetAdapter,
                executablePathsAdapter = StringSetAdapter,
                playStateAdapter = EnumColumnAdapter(),
            ),
            PlaySessionEntityAdapter = PlaySessionEntity.Adapter(
                gameIdAdapter = IntColumnAdapter,
            ),
        )
    }
}
