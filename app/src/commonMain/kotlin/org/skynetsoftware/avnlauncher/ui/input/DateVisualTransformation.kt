package org.skynetsoftware.avnlauncher.ui.input

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import org.skynetsoftware.avnlauncher.ui.screen.import.FIRST_PLAYED_DATE_CHAR_COUNT

class DateVisualTransformation(private val mask: String, private val divider: Char) :
    VisualTransformation {
    private val dateOffsetMapping = DateOffsetMapping(mask, divider)

    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = text.text.take(FIRST_PLAYED_DATE_CHAR_COUNT)
        val transformedBuilder = buildString {
            @Suppress("LoopWithTooManyJumpStatements")
            for (maskIndex in mask.indices) {
                val originalIndex = dateOffsetMapping.transformedToOriginal(maskIndex)
                if (mask[maskIndex] == divider) {
                    append(divider)
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
