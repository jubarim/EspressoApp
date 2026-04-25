package org.juba.espressoapp

import org.juba.espressoapp.extensions.normalizeDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** Verifies the comma→dot normalization logic used across decimal input fields. */
class DecimalNormalizationTest {

    @Test
    fun `dot input is unchanged`() {
        assertEquals("18.5", "18.5".normalizeDecimal())
    }

    @Test
    fun `comma is replaced with dot`() {
        assertEquals("18.5", "18,5".normalizeDecimal())
    }

    @Test
    fun `integer string is unchanged`() {
        assertEquals("18", "18".normalizeDecimal())
    }

    @Test
    fun `empty string is unchanged`() {
        assertEquals("", "".normalizeDecimal())
    }

    @Test
    fun `normalized value parses to expected double`() {
        assertEquals(18.5, "18,5".normalizeDecimal().toDoubleOrNull()!!, 0.0)
    }

    @Test
    fun `invalid input returns null after normalization`() {
        assertNull("abc".normalizeDecimal().toDoubleOrNull())
    }

    @Test
    fun `locale-style value with multiple commas returns null`() {
        assertNull("1,000,5".normalizeDecimal().toDoubleOrNull())
    }
}
