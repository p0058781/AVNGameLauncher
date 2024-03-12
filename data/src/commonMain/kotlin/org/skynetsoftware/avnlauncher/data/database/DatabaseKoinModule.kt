package org.skynetsoftware.avnlauncher.data.database

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.adapter.primitive.FloatColumnAdapter
import app.cash.sqldelight.adapter.primitive.IntColumnAdapter
import org.koin.core.module.Module
import org.skynetsoftware.avnlauncher.data.Database
import org.skynetsoftware.avnlauncher.data.GameEntity

private object StringSetAdapter : ColumnAdapter<Set<String>, String> {
    override fun decode(databaseValue: String) =
        if (databaseValue.isEmpty()) {
            setOf()
        } else {
            databaseValue.split(",").toSet()
        }

    override fun encode(value: Set<String>) = value.joinToString(separator = ",")
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
        )
    }
}
