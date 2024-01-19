package org.skynetsoftware.avnlauncher.data.repository

import io.realm.kotlin.MutableRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import io.realm.kotlin.ext.toRealmSet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import org.koin.core.module.Module
import org.skynetsoftware.avnlauncher.data.database.model.RealmGame
import org.skynetsoftware.avnlauncher.data.mapper.toGame
import org.skynetsoftware.avnlauncher.data.mapper.toRealmGame
import org.skynetsoftware.avnlauncher.domain.model.Game
import org.skynetsoftware.avnlauncher.domain.model.PlayState
import org.skynetsoftware.avnlauncher.domain.repository.GamesRepository

internal fun Module.gamesRepositoryKoinModule() {
    single<GamesRepository> {
        GamesRepositoryImpl(get())
    }
}

@Suppress("TooManyFunctions")
internal class GamesRepositoryImpl(
    private val realm: Realm,
) : GamesRepository {
    override val games: Flow<List<Game>> = realm.query<RealmGame>().find().asFlow().map { resultChange ->
        resultChange.list.map { it.toGame() }
    }

    override suspend fun all(): List<Game> {
        return realm.query<RealmGame>().find().map(RealmGame::toGame)
    }

    override suspend fun get(id: Int): Game? {
        return realm.query<RealmGame>("f95ZoneThreadId == $0", id).first().find()?.toGame()
    }

    override suspend fun updateRating(
        id: Int,
        rating: Int,
    ) = realmWrite {
        findRealmGame(id)?.rating = rating
    }

    override suspend fun updateExecutablePaths(games: List<Pair<Int, Set<String>>>) =
        realmWrite {
            games.forEach {
                findRealmGame(it.first)?.executablePaths = it.second.toRealmSet()
            }
        }

    override suspend fun insertGame(game: Game) =
        realmWrite {
            copyToRealm(game.toRealmGame().apply { added = Clock.System.now().toEpochMilliseconds() })
            Unit
        }

    override suspend fun updateGames(games: List<Game>) =
        realmWrite {
            games.forEach {
                copyToRealm(it.toRealmGame(), updatePolicy = UpdatePolicy.ALL)
            }
        }

    @Suppress("LongParameterList")
    override suspend fun updateGame(
        id: Int,
        title: String,
        executablePaths: Set<String>,
        imageUrl: String,
        checkForUpdates: Boolean,
        playState: PlayState,
        hidden: Boolean,
    ) = realmWrite {
        val realmGame = findRealmGame(id)
        realmGame?.checkForUpdates = checkForUpdates
        realmGame?.title = title
        realmGame?.imageUrl = imageUrl
        realmGame?.executablePaths = executablePaths.toRealmSet()
        realmGame?.playState = playState.name
        realmGame?.hidden = hidden
    }

    override suspend fun updateGame(
        id: Int,
        updateAvailable: Boolean,
        version: String,
        availableVersion: String?,
    ) = realmWrite {
        val realmGame = findRealmGame(id)
        realmGame?.updateAvailable = updateAvailable
        realmGame?.version = version
        realmGame?.availableVersion = availableVersion
    }

    override suspend fun updateGame(
        id: Int,
        playTime: Long,
        lastPlayed: Long,
    ) = realmWrite {
        val realmGame = findRealmGame(id)
        realmGame?.playTime = playTime
        realmGame?.lastPlayed = lastPlayed
    }

    private fun MutableRealm.findRealmGame(id: Int) = query<RealmGame>("f95ZoneThreadId == $0", id).first().find()

    private suspend fun <R> realmWrite(block: MutableRealm.() -> R): R {
        return realm.write(block)
    }
}
