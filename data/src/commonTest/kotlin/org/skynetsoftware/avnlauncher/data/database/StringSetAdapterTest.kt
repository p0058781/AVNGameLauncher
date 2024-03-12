package org.skynetsoftware.avnlauncher.data.database

import kotlin.test.Test
import kotlin.test.assertEquals

class StringSetAdapterTest {
    @Test
    fun `decode single value`() {
        val input = "value"
        val result = StringSetAdapter.decode(input)
        assertEquals(setOf(input), result)
    }

    @Test
    fun `decode multiple values`() {
        val input = "value,value2, value3"
        val result = StringSetAdapter.decode(input)
        assertEquals(setOf("value", "value2", "value3"), result)
    }

    @Test
    fun `decode empty`() {
        val input = ""
        val result = StringSetAdapter.decode(input)
        assertEquals(emptySet(), result)
    }

    @Test
    fun `decode space only`() {
        val input = " "
        val result = StringSetAdapter.decode(input)
        assertEquals(emptySet(), result)
    }

    @Test
    fun `decode comma only`() {
        val input = ","
        val result = StringSetAdapter.decode(input)
        assertEquals(emptySet(), result)
    }

    @Test
    fun `decode comma with spaces`() {
        val input = " , "
        val result = StringSetAdapter.decode(input)
        assertEquals(emptySet(), result)
    }

    @Test
    fun `decode leading comma`() {
        val input = " ,value"
        val result = StringSetAdapter.decode(input)
        assertEquals(setOf("value"), result)
    }

    @Test
    fun `decode trailing comma`() {
        val input = "value, "
        val result = StringSetAdapter.decode(input)
        assertEquals(setOf("value"), result)
    }

    @Test
    fun `decode space`() {
        val input = "value2 , value3, value4 "
        val result = StringSetAdapter.decode(input)
        assertEquals(setOf("value2", "value3", "value4"), result)
    }

    @Test
    fun `encode single value`() {
        val input = setOf("value")
        val result = StringSetAdapter.encode(input)
        assertEquals("value", result)
    }

    @Test
    fun `encode multiple values`() {
        val input = setOf("value", "value2", "value3")
        val result = StringSetAdapter.encode(input)
        assertEquals("value,value2,value3", result)
    }

    @Test
    fun `encode empty`() {
        val input = emptySet<String>()
        val result = StringSetAdapter.encode(input)
        assertEquals("", result)
    }

    @Test
    fun `encode space only`() {
        val input = setOf(" ")
        val result = StringSetAdapter.encode(input)
        assertEquals("", result)
    }

    @Test
    fun `encode comma with spaces`() {
        val input = setOf(" ", " ")
        val result = StringSetAdapter.encode(input)
        assertEquals("", result)
    }
}
