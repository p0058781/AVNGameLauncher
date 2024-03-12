package org.skynetsoftware.avnlauncher.logger

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.config.Configurator
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory
import org.koin.dsl.module
import org.skynetsoftware.avnlauncher.config.ConfigManager
import org.slf4j.LoggerFactory
import java.io.File

actual val loggerKoinModule = module {
    single<Logger> { LoggerImpl(Logger.Level.Info, get()) }
}

private class LoggerImpl(
    level: Logger.Level,
    configManager: ConfigManager,
) : Logger {
    init {
        val builder = ConfigurationBuilderFactory.newConfigurationBuilder()

        val console = builder.newAppender("stdout", "Console")

        val fileAppender = builder.newAppender("rolling", "RollingFile")
        fileAppender.addAttribute("fileName", File(configManager.dataDir, "log.txt").absolutePath)
        fileAppender.addAttribute("filePattern", File(configManager.dataDir, "log-%d{MM-dd-yy}.txt.gz").absolutePath)
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

        val rootLogger = builder.newRootLogger(level.toLog4JLevel())
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

    override fun error(throwable: Throwable) {
        val slf4jLogger = LoggerFactory.getLogger(getLoggerName())
        slf4jLogger.error(throwable.message, throwable)
    }
}

private fun Logger.Level.toLog4JLevel(): Level {
    return when (this) {
        Logger.Level.Verbose -> Level.TRACE
        Logger.Level.Debug -> Level.DEBUG
        Logger.Level.Info -> Level.INFO
        Logger.Level.Warning -> Level.WARN
        Logger.Level.Error -> Level.ERROR
    }
}
