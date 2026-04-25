package org.juba.espressoapp.data.local.mapper

import org.juba.espressoapp.data.local.entity.ShotLogEntity
import org.juba.espressoapp.data.local.entity.ShotLogWithDetails
import org.juba.espressoapp.domain.model.ShotLog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ShotLogMapperTest {

    private val entity = ShotLogEntity(
        id = "shot-1",
        coffeeBeanId = "bean-1",
        grinderId = "grinder-1",
        machineId = "machine-1",
        basketId = "basket-1",
        grindSetting = "3.2",
        doseGrams = 18.0,
        yieldGrams = 36.0,
        extractionTimeSeconds = 27,
        brewTemperatureCelsius = 93.5,
        preInfusionSeconds = 5,
        rating = 4,
        notes = "Tasty",
        shotAt = 1_000L,
        isDeleted = false,
        createdAt = 1_000L,
        updatedAt = 2_000L,
    )

    private val withDetails = ShotLogWithDetails(
        shot = entity,
        coffeeBeanName = "Ethiopia Yirgacheffe",
        roasterName = "Square Mile",
        grinderName = "Niche Zero",
        machineName = "ECM Synchronika",
        basketName = "IMS Precision",
    )

    private val domain = ShotLog(
        id = "shot-1",
        coffeeBeanId = "bean-1",
        coffeeBeanName = "Ethiopia Yirgacheffe",
        roasterName = "Square Mile",
        grinderId = "grinder-1",
        grinderName = "Niche Zero",
        machineId = "machine-1",
        machineName = "ECM Synchronika",
        basketId = "basket-1",
        basketName = "IMS Precision",
        grindSetting = "3.2",
        doseGrams = 18.0,
        yieldGrams = 36.0,
        ratio = 2.0,
        extractionTimeSeconds = 27,
        brewTemperatureCelsius = 93.5,
        preInfusionSeconds = 5,
        rating = 4,
        notes = "Tasty",
        shotAt = 1_000L,
        createdAt = 1_000L,
        updatedAt = 2_000L,
    )

    @Test
    fun `withDetails toDomain maps all fields`() {
        assertEquals(domain, withDetails.toDomain())
    }

    @Test
    fun `ratio is computed as yield divided by dose`() {
        val result = withDetails.toDomain()
        assertEquals(2.0, result.ratio, 0.0001)
    }

    @Test
    fun `ratio is zero when dose is zero`() {
        val zeroDose = withDetails.copy(shot = entity.copy(doseGrams = 0.0, yieldGrams = 36.0))
        assertEquals(0.0, zeroDose.toDomain().ratio, 0.0)
    }

    @Test
    fun `domain toEntity maps all fields and sets isDeleted false`() {
        val result = domain.toEntity()
        assertEquals(entity, result)
        assertEquals(false, result.isDeleted)
    }

    @Test
    fun `nullable fields survive round trip`() {
        val sparse = withDetails.copy(
            shot = entity.copy(
                grindSetting = null,
                extractionTimeSeconds = null,
                brewTemperatureCelsius = null,
                preInfusionSeconds = null,
                rating = null,
                notes = null,
            ),
            coffeeBeanName = null,
            roasterName = null,
            grinderName = null,
            machineName = null,
            basketName = null,
        )
        val result = sparse.toDomain()
        assertNull(result.grindSetting)
        assertNull(result.extractionTimeSeconds)
        assertNull(result.brewTemperatureCelsius)
        assertNull(result.preInfusionSeconds)
        assertNull(result.rating)
        assertNull(result.notes)
        assertNull(result.coffeeBeanName)
        assertNull(result.roasterName)
        assertNull(result.grinderName)
        assertNull(result.machineName)
        assertNull(result.basketName)
    }
}
