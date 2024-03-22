package org.skynetsoftware.avnlauncher.ui.input

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.SimpleDateFormat
import java.util.Locale

object DateVisualTransformation : VisualTransformation {
    private const val DEFAULT_MASK = "dd/MM/yyyy"
    private const val DEFAULT_MASK_INVERTED = "MM/dd/yyyy"
    private const val DIVIDER = '/'

    val MASK = if (Locale.getDefault(Locale.Category.FORMAT).country == Locale.US.country) {
        DEFAULT_MASK_INVERTED
    } else {
        DEFAULT_MASK
    }
    val CHAR_COUNT_WITHOUT_DIVIDER = MASK.replace(DIVIDER.toString(), "").count()

    private val dateOffsetMapping = DateOffsetMapping(MASK, DIVIDER)

    fun getUnmaskedDateFormat(): SimpleDateFormat {
        return SimpleDateFormat(MASK.replace(DIVIDER.toString(), ""))
    }

    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = text.text.take(CHAR_COUNT_WITHOUT_DIVIDER)
        val transformedBuilder = buildString {
            @Suppress("LoopWithTooManyJumpStatements")
            for (maskIndex in MASK.indices) {
                val originalIndex = dateOffsetMapping.transformedToOriginal(maskIndex)
                if (MASK[maskIndex] == DIVIDER) {
                    append(DIVIDER)
                    continue
                }
                if (originalIndex >= trimmed.length) {
                    break
                }
                append(trimmed[originalIndex])
            }
        }

        return TransformedText(
            text = AnnotatedString(transformedBuilder),
            offsetMapping = dateOffsetMapping,
        )
    }
}
