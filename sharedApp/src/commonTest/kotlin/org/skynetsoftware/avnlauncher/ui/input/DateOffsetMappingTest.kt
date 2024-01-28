package org.skynetsoftware.avnlauncher.ui.input

import kotlin.test.Test
import kotlin.test.assertEquals

class DateOffsetMappingTest {
    @Test
    fun `originalToTransformed standard date and mask`() {
        val mapping = DateOffsetMapping("dd/MM/yyyy", '/')
        assertEquals(0, mapping.originalToTransformed(0))
        assertEquals(1, mapping.originalToTransformed(1))
        assertEquals(3, mapping.originalToTransformed(2))
        assertEquals(4, mapping.originalToTransformed(3))
        assertEquals(6, mapping.originalToTransformed(4))
        assertEquals(7, mapping.originalToTransformed(5))
        assertEquals(8, mapping.originalToTransformed(6))
        assertEquals(9, mapping.originalToTransformed(7))
    }

    @Test
    fun `originalToTransformed no divider`() {
        val mapping = DateOffsetMapping("ddMMyyyy", '/')
        (0 until 8).forEach {
            assertEquals(it, mapping.originalToTransformed(it))
        }
    }

    @Test
    fun `originalToTransformed mask`() {
        val mapping = DateOffsetMapping("", '/')
        (0 until 8).forEach {
            assertEquals(it, mapping.originalToTransformed(it))
        }
    }

    @Test
    fun `transformedToOriginal standard date and mask`() {
        val mapping = DateOffsetMapping("dd/MM/yyyy", '/')
        assertEquals(0, mapping.transformedToOriginal(0))
        assertEquals(1, mapping.transformedToOriginal(1))
        assertEquals(1, mapping.transformedToOriginal(2))
        assertEquals(2, mapping.transformedToOriginal(3))
        assertEquals(3, mapping.transformedToOriginal(4))
        assertEquals(3, mapping.transformedToOriginal(5))
        assertEquals(4, mapping.transformedToOriginal(6))
        assertEquals(5, mapping.transformedToOriginal(7))
        assertEquals(6, mapping.transformedToOriginal(8))
        assertEquals(7, mapping.transformedToOriginal(9))
    }

    @Test
    fun `transformedToOriginal no divider`() {
        val mapping = DateOffsetMapping("ddMMyyyy", '/')
        (0 until 8).forEach {
            assertEquals(it, mapping.transformedToOriginal(it))
        }
    }

    @Test
    fun `transformedToOriginal mask`() {
        val mapping = DateOffsetMapping("", '/')
        (0 until 8).forEach {
            assertEquals(it, mapping.transformedToOriginal(it))
        }
    }
}
