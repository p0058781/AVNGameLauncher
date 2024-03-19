package org.skynetsoftware.avnlauncher.ui.input

import androidx.compose.ui.text.input.OffsetMapping

// dd/MM/yyyy
class DateOffsetMapping(private val mask: String, private val divider: Char) : OffsetMapping {
    override fun originalToTransformed(offset: Int): Int {
        if (mask.isEmpty()) return offset
        var divCount = 0
        var i = 0
        while (i <= offset && i + divCount < mask.length) {
            if (mask[i + divCount] == divider) {
                divCount++
            } else {
                i++
            }
        }
        return offset + divCount
    }

    override fun transformedToOriginal(offset: Int): Int {
        if (mask.isEmpty()) return offset
        var divCount = 0
        var i = 0
        while (i <= offset && i + divCount < mask.length) {
            if (mask[i] == divider) {
                divCount++
            }
            i++
        }
        return offset - divCount
    }
}
