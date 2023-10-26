package org.skynetsoftware.avnlauncher.utils

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.cache.memory.maxSizePercent
import com.seiko.imageloader.component.setupDefaultComponents
import com.seiko.imageloader.util.DebugLogger
import com.seiko.imageloader.util.LogPriority
import kotlinx.coroutines.Dispatchers
import okio.Path.Companion.toOkioPath
import org.skynetsoftware.avnlauncher.config.ConfigManager
import java.awt.Dimension
import java.awt.Toolkit

fun getDefaultWindowSize(): DpSize {
    val screenSize: Dimension = Toolkit.getDefaultToolkit().screenSize
    val width: Int = (screenSize.width * 0.6f).toInt()
    val height: Int = (screenSize.height * 0.6f).toInt()
    return DpSize(width.dp, height.dp)
}

fun imageLoader(configManager: ConfigManager) = ImageLoader(requestCoroutineContext = Dispatchers.IO) {
    logger = DebugLogger(LogPriority.WARN)
    components {
        setupDefaultComponents()
    }
    interceptor {
        memoryCacheConfig {
            maxSizePercent(0.25)
        }
        diskCacheConfig {
            directory(configManager.cacheDir.toOkioPath())
            maxSizeBytes(512L * 1024 * 1024) // 512MB
        }
    }
}