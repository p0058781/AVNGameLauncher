package org.skynetsoftware.avnlauncher.imageloader

import coil3.ImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.asImage
import coil3.decode.DecodeResult
import coil3.decode.DecodeUtils
import coil3.decode.Decoder
import coil3.decode.ImageSource
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import coil3.request.maxBitmapSize
import coil3.size.Precision
import coil3.util.component1
import coil3.util.component2
import okio.use
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Image
import org.jetbrains.skia.Rect
import org.jetbrains.skia.impl.use
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

/**
 * This class is a gross workaround for Skia/Skiko not supporting avif images
 * This will decode the source using ImageIO, with the help of https://github.com/umjammer/vavi-image-avif
 * and re-encode to png, after which image will be decoded again using skia
 * TODO remove this as soon as skia adds support for avif images
 *
 * Source encoding is not checked either, so even if format is supported ti will still be decoded/re-encoded/decoded
 */
class ImageIOReEncodeDecoder(
    private val source: ImageSource,
    private val options: Options,
) : Decoder {
    override suspend fun decode(): DecodeResult {
        val decodedImage = source.source().inputStream().use { ImageIO.read(it) }
        val pngImageBytes = ByteArrayOutputStream().apply {
            ImageIO.write(decodedImage, "png", this)
        }.toByteArray()
        val image = Image.makeFromEncoded(pngImageBytes)
        // TODO instead of encoding to png and decoding again, directly decode from pixel data (this will require
        // color types mapping between imageio and skia)

        val isSampled: Boolean
        val bitmap: Bitmap
        try {
            bitmap = Bitmap.makeFromImage(image, options)
            bitmap.setImmutable()
            isSampled = bitmap.width < image.width || bitmap.height < image.height
        } finally {
            image.close()
        }

        return DecodeResult(
            image = bitmap.asImage(),
            isSampled = isSampled,
        )
    }

    class Factory : Decoder.Factory {
        override fun create(
            result: SourceFetchResult,
            options: Options,
            imageLoader: ImageLoader,
        ): Decoder {
            return ImageIOReEncodeDecoder(result.source, options)
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
fun Bitmap.Companion.makeFromImage(
    image: Image,
    options: Options,
): Bitmap {
    val srcWidth = image.width
    val srcHeight = image.height
    val (dstWidth, dstHeight) = DecodeUtils.computeDstSize(
        srcWidth = srcWidth,
        srcHeight = srcHeight,
        targetSize = options.size,
        scale = options.scale,
        maxSize = options.maxBitmapSize,
    )
    var multiplier = DecodeUtils.computeSizeMultiplier(
        srcWidth = srcWidth,
        srcHeight = srcHeight,
        dstWidth = dstWidth,
        dstHeight = dstHeight,
        scale = options.scale,
    )

    // Only upscale the image if the options require an exact size.
    if (options.precision == Precision.INEXACT) {
        multiplier = multiplier.coerceAtMost(1.0)
    }

    val outWidth = (multiplier * srcWidth).toInt()
    val outHeight = (multiplier * srcHeight).toInt()

    val bitmap = Bitmap()
    bitmap.allocN32Pixels(outWidth, outHeight)
    Canvas(bitmap).use { canvas ->
        canvas.drawImageRect(
            image = image,
            src = Rect.makeWH(srcWidth.toFloat(), srcHeight.toFloat()),
            dst = Rect.makeWH(outWidth.toFloat(), outHeight.toFloat()),
        )
    }
    return bitmap
}
