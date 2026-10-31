package org.juba.espressoapp.extensions

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.juba.espressoapp.domain.model.ShotLog

class ShotLogExtensionsTest {

    @Test
    fun `duplicate gives the copy a new identity and timestamps`() {
        val original = shot(id = "shot-1", shotAt = 1_000L, createdAt = 1_000L, updatedAt = 2_000L)

        val copy = original.duplicate(shotAt = 5_000L, now = 9_000L)

        assertNotEquals(original.id, copy.id)
        assertEquals(5_000L, copy.shotAt)
        assertEquals(9_000L, copy.createdAt)
        assertEquals(9_000L, copy.updatedAt)
    }

    @Test
    fun `duplicate keeps every other field`() {
        val original = shot(id = "shot-1", shotAt = 1_000L, createdAt = 1_000L, updatedAt = 2_000L)

        val copy = original.duplicate(shotAt = 5_000L, now = 9_000L)

        assertEquals(original.coffeeBeanId, copy.coffeeBeanId)
        assertEquals(original.coffeeBeanName, copy.coffeeBeanName)
        assertEquals(original.grinderId, copy.grinderId)
        assertEquals(original.doseGrams, copy.doseGrams, 0.0)
        assertEquals(original.yieldGrams, copy.yieldGrams, 0.0)
        assertEquals(original.rating, copy.rating)
        assertEquals(original.notes, copy.notes)
    }

    private fun shot(
        id: String,
        shotAt: Long,
        createdAt: Long,
        updatedAt: Long,
    ): ShotLog = ShotLog(
        id = id,
        coffeeBeanId = "bean-1",
        coffeeBeanName = "Ethiopia",
        roasterName = "Square Mile",
        grinderId = "grinder-1",
        grinderName = "Niche Zero",
        machineId = "machine-1",
        machineName = "Decent DE1",
        basketId = "basket-1",
        basketName = "IMS 18g",
        basketSizeClass = null,
        grindSetting = "2.5",
        doseGrams = 18.0,
        yieldGrams = 36.0,
        ratio = 2.0,
        extractionTimeSeconds = 30,
        brewTemperatureCelsius = 93.5,
        preInfusionSeconds = 5,
        rating = 4,
        notes = "Bright",
        shotAt = shotAt,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}
