package org.skynetsoftware.avnlauncher.data.repository

import com.russhwolf.settings.Settings
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.skynetsoftware.avnlauncher.config.ConfigManager
import org.skynetsoftware.avnlauncher.domain.model.Filter
import org.skynetsoftware.avnlauncher.domain.model.SortDirection
import org.skynetsoftware.avnlauncher.domain.model.SortOrder
import org.skynetsoftware.avnlauncher.domain.repository.SettingsRepository
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SettingsRepositorySharedTest : KoinTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = UnconfinedTestDispatcher()

    private val settingsMock = mockk<Settings>(relaxed = true)

    private val settingsRepository by inject<SettingsRepository>()

    private val configManager = mockk<ConfigManager>()

    @BeforeTest
    fun setup() {
        startKoin {
            modules(
                module {
                    settingsKoinModule()
                    single<Settings> { settingsMock }
                    single<ConfigManager> { configManager }
                },
            )
        }
        every { configManager.selectedFilterDefault } returns Filter.All
        every { configManager.selectedSortOrderDefault } returns SortOrder.LastPlayed
        every { configManager.selectedSortOrderDirectionDefault } returns SortDirection.Descending
        every { configManager.sfwModeEnabledDefault } returns false
        every { configManager.periodicUpdateChecksDefault } returns true
        every { configManager.minimizeToTrayOnCloseDefault } returns false
    }

    @AfterTest
    fun teardown() {
        stopKoin()
    }

    @Test
    fun `selectedFilter returns correct default value`() =
        runTest(testDispatcher) {
            val expected = Filter.All

            val actual = settingsRepository.selectedFilter.first()

            assertEquals(expected, actual)
        }

    @Test
    fun `selectedFilter returns correct value`() =
        runTest(testDispatcher) {
            val expected = Filter.GamesWithUpdate

            every { settingsMock.getString("selectedFilter", any()) } returns expected::class.simpleName!!

            val actual = settingsRepository.selectedFilter.first()

            assertEquals(expected, actual)
        }
}
