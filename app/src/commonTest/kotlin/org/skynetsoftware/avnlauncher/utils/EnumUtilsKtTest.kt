package org.skynetsoftware.avnlauncher.utils

import kotlin.test.Test
import kotlin.test.assertEquals

class EnumUtilsKtTest {
    private enum class TestEnum {
        A,
        B,
        C,
        D,
        E,
        F,
    }

    private enum class SingleElementEnum {
        A,
    }

    @Test
    fun `next returns correct next value`() {
        val input = TestEnum.C
        val next = input.next()
        assertEquals(TestEnum.D, next)
    }

    @Test
    fun `next wraps around correctly`() {
        val input = TestEnum.F
        val next = input.next()
        assertEquals(TestEnum.A, next)
    }

    @Test
    fun `next for single element works`() {
        val input = SingleElementEnum.A
        val next = input.next()
        assertEquals(SingleElementEnum.A, next)
    }

    @Test
    fun `previous returns correct previous value`() {
        val input = TestEnum.C
        val next = input.previous()
        assertEquals(TestEnum.B, next)
    }

    @Test
    fun `previous wraps around correctly`() {
        val input = TestEnum.A
        val next = input.previous()
        assertEquals(TestEnum.F, next)
    }

    @Test
    fun `previous for single element works`() {
        val input = SingleElementEnum.A
        val next = input.previous()
        assertEquals(SingleElementEnum.A, next)
    }
}
