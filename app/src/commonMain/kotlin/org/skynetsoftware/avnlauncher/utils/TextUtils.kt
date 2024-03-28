package org.skynetsoftware.avnlauncher.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import java.util.regex.Pattern

fun String.highlightRegions(region: String) =
    buildAnnotatedString {
        val input = this@highlightRegions
        val highlightRanges = Regex(Pattern.quote(region.lowercase()))
            .findAll(input.lowercase())
            .map { it.range.first to it.range.first + region.length }.toList()
        if (highlightRanges.isEmpty()) {
            append(input)
        } else {
            var start = 0
            var highlightRangeIndex = 0
            while (start < input.length && highlightRangeIndex < highlightRanges.size) {
                val highlightRange = highlightRanges[highlightRangeIndex]
                append(input.substring(start, highlightRange.first))
                withStyle(SpanStyle(color = Color.Yellow)) {
                    append(input.substring(highlightRange.first, highlightRange.second))
                }
                start = highlightRange.second
                highlightRangeIndex++
            }
            if (start < input.length) {
                append(input.substring(start, input.length))
            }
        }
    }
