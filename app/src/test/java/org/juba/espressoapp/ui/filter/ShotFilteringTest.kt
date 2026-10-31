package org.juba.espressoapp.ui.filter

import org.junit.Assert.assertEquals
import org.junit.Test
import org.juba.espressoapp.domain.model.CoffeeBean
import org.juba.espressoapp.domain.model.ShotLog

class ShotFilteringTest {

    private val beans = listOf(
        bean(id = "bean-1", roasterId = "roaster-1"),
        bean(id = "bean-2", roasterId = "roaster-1"),
        bean(id = "bean-3", roasterId = "roaster-2"),
    )

    private val shots = listOf(
        shot(id = "shot-1", coffeeBeanId = "bean-1"),
        shot(id = "shot-2", coffeeBeanId = "bean-2"),
        shot(id = "shot-3", coffeeBeanId = "bean-3"),
        shot(id = "shot-4", coffeeBeanId = "bean-1"),
    )

    @Test
    fun `null filter keeps every shot in order`() {
        assertEquals(shots, shots.filteredBy(null, beans))
    }

    @Test
    fun `coffee filter matches the exact bean record`() {
        val filter = ShotFilter.CoffeeBean(coffeeBeanId = "bean-1", label = "Ethiopia")

        val result = shots.filteredBy(filter, beans)

        assertEquals(listOf("shot-1", "shot-4"), result.map { it.id })
    }

    @Test
    fun `coffee filter with an unknown bean returns nothing`() {
        val filter = ShotFilter.CoffeeBean(coffeeBeanId = "missing", label = "Unknown")

        assertEquals(emptyList<ShotLog>(), shots.filteredBy(filter, beans))
    }

    @Test
    fun `roaster filter matches every bean from that roaster`() {
        val filter = ShotFilter.Roaster(roasterId = "roaster-1", label = "Square Mile")

        val result = shots.filteredBy(filter, beans)

        assertEquals(listOf("shot-1", "shot-2", "shot-4"), result.map { it.id })
    }

    @Test
    fun `roaster filter with no beans returns nothing`() {
        val filter = ShotFilter.Roaster(roasterId = "missing", label = "Unknown")

        assertEquals(emptyList<ShotLog>(), shots.filteredBy(filter, beans))
    }

    @Test
    fun `roaster filter ignores shots whose bean is missing`() {
        val orphan = shot(id = "shot-5", coffeeBeanId = "missing")
        val filter = ShotFilter.Roaster(roasterId = "roaster-1", label = "Square Mile")

        val result = (shots + orphan).filteredBy(filter, beans)

        assertEquals(listOf("shot-1", "shot-2", "shot-4"), result.map { it.id })
    }

    private fun shot(id: String, coffeeBeanId: String): ShotLog = ShotLog(
        id = id,
        coffeeBeanId = coffeeBeanId,
        coffeeBeanName = null,
        roasterName = null,
        grinderId = "grinder-1",
        grinderName = null,
        machineId = "machine-1",
        machineName = null,
        basketId = "basket-1",
        basketName = null,
        basketSizeClass = null,
        grindSetting = null,
        doseGrams = 18.0,
        yieldGrams = 36.0,
        ratio = 2.0,
        extractionTimeSeconds = null,
        brewTemperatureCelsius = null,
        preInfusionSeconds = null,
        rating = null,
        notes = null,
        shotAt = 0L,
        createdAt = 0L,
        updatedAt = 0L,
    )

    private fun bean(id: String, roasterId: String): CoffeeBean = CoffeeBean(
        id = id,
        roasterId = roasterId,
        roasterName = null,
        name = id,
        origin = null,
        process = null,
        roastLevel = null,
        roastDate = null,
        imageUri = null,
        notes = null,
        createdAt = 0L,
        updatedAt = 0L,
    )
}
