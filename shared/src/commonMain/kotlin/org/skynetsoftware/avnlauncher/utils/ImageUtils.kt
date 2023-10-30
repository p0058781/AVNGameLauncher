package org.skynetsoftware.avnlauncher.utils

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.cache.memory.maxSizePercent
import com.seiko.imageloader.component.setupCommonComponents
import com.seiko.imageloader.component.setupDefaultComponents
import com.seiko.imageloader.util.DebugLogger
import com.seiko.imageloader.util.LogPriority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import okio.Path.Companion.toOkioPath
import org.skynetsoftware.avnlauncher.config.Config
import org.skynetsoftware.avnlauncher.config.ConfigManager

fun imageLoader(configManager: ConfigManager) = ImageLoader(requestCoroutineContext = Dispatchers.IO) {
    logger = DebugLogger(LogPriority.WARN)
    components {
        setupCommonComponents()
    }
    interceptor {
        memoryCacheConfig {
            maxSizePercent(0.25)
        };
        when(val cacheDir = configManager.cacheDir) {
            is Config.None -> {
                //TODO log warning
            }
            is Config.Some -> {
                diskCacheConfig {
                    directory(cacheDir.value)
                    maxSizeBytes(512L * 1024 * 1024) // 512MB
                }
            }
        }
    }
}