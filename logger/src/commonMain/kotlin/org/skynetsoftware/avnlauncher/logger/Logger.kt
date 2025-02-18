package org.skynetsoftware.avnlauncher.logger

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.config.Configurator
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.config.Config
import org.skynetsoftware.avnlauncher.domain.coroutines.CoroutineDispatchers
import org.skynetsoftware.avnlauncher.domain.model.LogLevel
import org.skynetsoftware.avnlauncher.domain.repository.SettingsRepository
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.system.exitProcess

fun logUncaughtExceptions(logger: Logger) {
    Thread.setDefaultUncaughtExceptionHandler { _, e ->
        logger.error(e)
        exitProcess(-1)
    }
}

val loggerKoinModule = module {
    single<Logger> { LoggerImpl(get(), get(), get()) }
}

private class LoggerImpl(
    config: Config,
    settingsRepository: SettingsRepository,
    coroutineDispatchers: CoroutineDispatchers,
) : Logger {
    private val coroutineScope = CoroutineScope(SupervisorJob() + coroutineDispatchers.default)

    init {
        val builder = ConfigurationBuilderFactory.newConfigurationBuilder()

        val console = builder.newAppender("stdout", "Console")

        val fileAppender = builder.newAppender("rolling", "RollingFile")
        fileAppender.addAttribute("fileName", File(config.dataDir, "log.txt").absolutePath)
        fileAppender.addAttribute("filePattern", File(config.dataDir, "log-%d{MM-dd-yy}.txt.gz").absolutePath)
        val triggeringPolicies = builder.newComponent("Policies")
            .addComponent(
                builder.newComponent("CronTriggeringPolicy")
                    .addAttribute("schedule", "0 0 0 * * ?"),
            )
            .addComponent(
                builder.newComponent("SizeBasedTriggeringPolicy")
                    .addAttribute("size", "10M"),
            )
        fileAppender.addComponent(triggeringPolicies)

        val rootLogger = builder.newRootLogger()
        rootLogger.add(builder.newAppenderRef("stdout"))
        rootLogger.add(builder.newAppenderRef("rolling"))

        val layout = builder.newLayout("PatternLayout")
        layout.addAttribute("pattern", "%d [%c{1}] %-5level: %msg%n%throwable")
        console.add(layout)
        fileAppender.add(layout)

        builder.add(console)
        builder.add(fileAppender)
        builder.add(rootLogger)

        Configurator.initialize(builder.build())

        coroutineScope.launch {
            settingsRepository.logLevel.collect {
                Configurator.setRootLevel(it.toLog4JLevel())
            }
        }
    }

    override fun verbose(message: String) {
        val slf4jLogger = LoggerFactory.getLogger(getLoggerName())
        slf4jLogger.trace(message)
    }

    override fun debug(message: String) {
        val slf4jLogger = LoggerFactory.getLogger(getLoggerName())
        slf4jLogger.debug(message)
    }

    override fun info(message: String) {
        val slf4jLogger = LoggerFactory.getLogger(getLoggerName())
        slf4jLogger.info(message)
    }

    override fun warning(message: String) {
        val slf4jLogger = LoggerFactory.getLogger(getLoggerName())
        slf4jLogger.warn(message)
    }

    override fun error(message: String) {
        val slf4jLogger = LoggerFactory.getLogger(getLoggerName())
        slf4jLogger.error(message)
    }

    override fun error(
        throwable: Throwable,
        message: String?,
    ) {
        val slf4jLogger = LoggerFactory.getLogger(getLoggerName())
        slf4jLogger.error(message ?: throwable.message, throwable)
    }
}

private fun LogLevel.toLog4JLevel(): Level {
    return when (this) {
        LogLevel.Verbose -> Level.TRACE
        LogLevel.Debug -> Level.DEBUG
        LogLevel.Info -> Level.INFO
        LogLevel.Warning -> Level.WARN
        LogLevel.Error -> Level.ERROR
    }
}

interface Logger {
    fun verbose(message: String)

    fun debug(message: String)

    fun info(message: String)

    fun warning(message: String)

    fun error(message: String)

    fun error(
        throwable: Throwable,
        message: String? = null,
    )
}

internal fun Logger.getLoggerName(): String {
    // iterate stacktrace until we find first occurrence of LoggerImpl
    // then find first next element that is not LoggerImpl
    val stacktrace = Thread.currentThread().stackTrace
    var stacktraceElement: StackTraceElement? = null
    var foundLogger = false
    for (element in stacktrace) {
        if (foundLogger) {
            if (element.className != this::class.qualifiedName) {
                stacktraceElement = element
                break
            }
        } else {
            if (element.className == this::class.qualifiedName) {
                foundLogger = true
            }
        }
    }
    return stacktraceElement?.className ?: "Logger"
}
