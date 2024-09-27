package org.skynetsoftware.avnlauncher.imageloader

import coil3.ImageLoader
import coil3.PlatformContext
import org.koin.core.module.Module

expect fun imageLoaderKoinModule(): Module

interface ImageLoaderFactory {
    fun createImageLoader(
        animateGifs: Boolean,
        platformContext: PlatformContext,
    ): ImageLoader
}


