package org.juba.espressoapp.extensions

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.juba.espressoapp.domain.model.CoffeeBean

class CoffeeBeanExtensionsTest {

    @Test
    fun `duplicate gives the copy a new identity and timestamps`() {
        val original = bean(id = "bean-1", roastDate = 1_000L, createdAt = 1_000L, updatedAt = 2_000L)

        val copy = original.duplicate(roastDate = 5_000L, now = 9_000L)

        assertNotEquals(original.id, copy.id)
        assertEquals(5_000L, copy.roastDate)
        assertEquals(9_000L, copy.createdAt)
        assertEquals(9_000L, copy.updatedAt)
    }

    @Test
    fun `duplicate keeps every other field`() {
        val original = bean(id = "bean-1", roastDate = 1_000L, createdAt = 1_000L, updatedAt = 2_000L)

        val copy = original.duplicate(roastDate = null, now = 9_000L)

        assertEquals(original.roasterId, copy.roasterId)
        assertEquals(original.name, copy.name)
        assertEquals(original.origin, copy.origin)
        assertEquals(original.roastLevel, copy.roastLevel)
        assertEquals(original.notes, copy.notes)
        assertNull(copy.roastDate)
    }

    private fun bean(
        id: String,
        roastDate: Long?,
        createdAt: Long,
        updatedAt: Long,
    ): CoffeeBean = CoffeeBean(
        id = id,
        roasterId = "roaster-1",
        roasterName = "Square Mile",
        name = "Ethiopia",
        origin = "Ethiopia",
        process = "Washed",
        roastLevel = "Light",
        roastDate = roastDate,
        imageUri = null,
        notes = "Bright",
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}
