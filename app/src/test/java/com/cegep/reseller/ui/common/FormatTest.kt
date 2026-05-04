package com.cegep.reseller.ui.common

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FormatTest {

    @Test
    fun parsesIntegerInput() {
        assertEquals(2500L, parsePriceToCents("25"))
    }

    @Test
    fun parsesDotDecimal() {
        assertEquals(1099L, parsePriceToCents("10.99"))
    }

    @Test
    fun parsesCommaDecimal() {
        assertEquals(1099L, parsePriceToCents("10,99"))
    }

    @Test
    fun trimsWhitespace() {
        assertEquals(500L, parsePriceToCents("  5.00 "))
    }

    @Test
    fun rejectsNegative() {
        assertNull(parsePriceToCents("-5"))
    }

    @Test
    fun rejectsBlank() {
        assertNull(parsePriceToCents(""))
    }

    @Test
    fun rejectsNonNumeric() {
        assertNull(parsePriceToCents("abc"))
    }

    @Test
    fun zeroIsAllowed() {
        assertEquals(0L, parsePriceToCents("0"))
    }
}
