package org.skynetsoftware.avnlauncher.ui.input

import androidx.compose.ui.text.AnnotatedString
import kotlin.test.Test
import kotlin.test.assertEquals

class DateVisualTransformationTest {
    private val transformation = DateVisualTransformation("dd/MM/yyyy", '/')

    @Test
    fun `filter full input`() {
        assertEquals("25/05/1991", transformation.filter(AnnotatedString("25051991")).text.text)
    }

    @Test
    fun `filter partial input`() {
        assertEquals("25/", transformation.filter(AnnotatedString("25")).text.text)
        assertEquals("25/0", transformation.filter(AnnotatedString("250")).text.text)
        assertEquals("25/05/", transformation.filter(AnnotatedString("2505")).text.text)
        assertEquals("25/05/1", transformation.filter(AnnotatedString("25051")).text.text)
    }

    @Test
    fun `filter no input`() {
        assertEquals("", transformation.filter(AnnotatedString("")).text.text)
    }

    @Test
    fun `filter longer input`() {
        assertEquals("25/05/1991", transformation.filter(AnnotatedString("25051991000000")).text.text)
    }
}
