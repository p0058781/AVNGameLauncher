package org.skynetsoftware.avnlauncher.data.repository

import io.realm.kotlin.MutableRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import io.realm.kotlin.ext.realmSetOf
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

internal class GamesRepositoryImpl(
    private val realm: Realm,
) : GamesRepository {
    override val games: Flow<List<Game>> = realm.query<RealmGame>().find().asFlow().map { resultChange ->
        resultChange.list.map { it.toGame() }
    }

    override suspend fun all(): List<Game> {
        return realm.query<RealmGame>().find().map(RealmGame::toGame)
    }

    override suspend fun updatePlayTime(
        id: Int,
        playTime: Long,
    ) = realmWrite {
        findRealmGame(id)?.playTime = playTime
    }

    override suspend fun updateLastPlayed(
        id: Int,
        lastPlayed: Long,
    ) = realmWrite {
        findRealmGame(id)?.lastPlayed = lastPlayed
    }

    override suspend fun updateLastUpdateCheck(
        id: Int,
        lastUpdateCheck: Long,
    ) = realmWrite {
        findRealmGame(id)?.lastUpdateCheck = lastUpdateCheck
    }

    override suspend fun updateUpdateAvailable(
        id: Int,
        updateAvailable: Boolean,
    ) = realmWrite {
        findRealmGame(id)?.updateAvailable = updateAvailable
    }

    override suspend fun updateAvailableVersion(
        id: Int,
        availableVersion: String?,
    ) = realmWrite {
        findRealmGame(id)?.availableVersion = availableVersion
    }

    override suspend fun updateVersion(
        id: Int,
        version: String,
    ) = realmWrite {
        findRealmGame(id)?.version = version
    }

    override suspend fun updateRating(
        id: Int,
        rating: Int,
    ) = realmWrite {
        findRealmGame(id)?.rating = rating
    }

    override suspend fun updateHidden(
        id: Int,
        hidden: Boolean,
    ) = realmWrite {
        findRealmGame(id)?.hidden = hidden
    }

    override suspend fun updatePlayState(
        id: Int,
        playState: PlayState,
    ) = realmWrite {
        findRealmGame(id)?.playState = playState.name
    }

    override suspend fun updateExecutablePaths(
        id: Int,
        executablePaths: Set<String>,
    ) = realmWrite {
        findRealmGame(id)?.executablePaths = realmSetOf(*executablePaths.toTypedArray())
    }

    override suspend fun updateExecutablePaths(games: List<Pair<Int, Set<String>>>) =
        realmWrite {
            games.forEach {
                findRealmGame(it.first)?.executablePaths = realmSetOf(*it.second.toTypedArray())
            }
        }

    override suspend fun updateTitle(
        id: Int,
        title: String,
    ) = realmWrite {
        findRealmGame(id)?.title = title
    }

    override suspend fun updateImageUrl(
        id: Int,
        imageUrl: String,
    ) = realmWrite {
        findRealmGame(id)?.imageUrl = imageUrl
    }

    override suspend fun updateReleaseDate(
        id: Int,
        releaseDate: Long,
    ) = realmWrite {
        findRealmGame(id)?.releaseDate = releaseDate
    }

    override suspend fun updateFirstReleaseDate(
        id: Int,
        firstReleaseDate: Long,
    ) = realmWrite {
        findRealmGame(id)?.firstReleaseDate = firstReleaseDate
    }

    override suspend fun updateLastRedirectUrl(
        id: Int,
        lastRedirectUrl: String,
    ) = realmWrite {
        findRealmGame(id)?.lastRedirectUrl = lastRedirectUrl
    }

    override suspend fun updateCheckForUpdates(
        id: Int,
        checkForUpdates: Boolean,
    ) = realmWrite {
        findRealmGame(id)?.checkForUpdates = checkForUpdates
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

    override suspend fun updateGame(
        id: Int,
        title: String,
        executablePaths: Set<String>,
        imageUrl: String,
        checkForUpdates: Boolean,
    ) = realmWrite {
        val realmGame = findRealmGame(id)
        realmGame?.checkForUpdates = checkForUpdates
        realmGame?.title = title
        realmGame?.imageUrl = imageUrl
        realmGame?.executablePaths = realmSetOf(*executablePaths.toTypedArray())
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
