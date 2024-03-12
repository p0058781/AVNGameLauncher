package org.skynetsoftware.avnlauncher.config

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.properties.Properties
import kotlinx.serialization.properties.decodeFromStringMap
import kotlinx.serialization.properties.encodeToStringMap
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.data.model.Config
import java.io.File

val configManagerKoinModule = module {
    single<ConfigManager> { ConfigManagerImpl() }
}

interface ConfigManager {
    val databaseFile: File
    val cacheDir: File
    val gamesDir: File
}

@OptIn(ExperimentalSerializationApi::class)
private class ConfigManagerImpl : ConfigManager {

    private val rootDir = File(System.getProperty("user.home"), ".config${File.separator}avnlauncher")
    private val configFile = File(rootDir, "config.properties")

    override val databaseFile: File
    override val cacheDir: File
    override val gamesDir: File

    init {
        rootDir.mkdirs()

        val config = if(!configFile.exists()) {
            createConfigWithDefaultValues()
        } else {
            readConfigFromFile()
        }

        databaseFile = config.databaseFile
        cacheDir = config.cacheDir
        gamesDir = config.gamesDir

        cacheDir.mkdirs()
    }

    private fun createConfigWithDefaultValues(): Config {
        val config = Config(
            databaseFile = File(rootDir, "avnlauncher.db"),
            cacheDir = File(rootDir, "cache"),
            gamesDir = File(System.getProperty("user.home"), "Downloads")
        )
        writeConfigToFile(config)
        return config
    }

    private fun readConfigFromFile(): Config {
        val properties = configFile.readLines().associate {
            val pair = it.split("=")
            pair[0] to pair[1]
        }
        return Properties.decodeFromStringMap(properties)
    }

    private fun writeConfigToFile(config: Config) {
        val contents = Properties.encodeToStringMap(config).entries.joinToString(separator = "\n") { "${it.key}=${it.value}" }
        configFile.writeText(contents)
    }
}