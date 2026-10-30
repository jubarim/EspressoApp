package org.juba.espressoapp.data.remote

import org.juba.espressoapp.data.local.entity.FilterBasketEntity
import org.juba.espressoapp.data.local.entity.ShotLogEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SyncMappersTest {

    @Test
    fun `shot log maps every field and writes ints as longs`() {
        val document = shotLog().toRemoteDocument()

        assertEquals(SyncCollection.SHOT_LOGS, document.collection)
        assertEquals("shot-1", document.id)
        assertEquals("bean-1", document.fields["coffeeBeanId"])
        assertEquals("basket-1", document.fields["basketId"])
        assertEquals(18.0, document.fields["doseGrams"])
        assertEquals(36.0, document.fields["yieldGrams"])
        assertEquals(27L, document.fields["extractionTimeSeconds"])
        assertEquals(5L, document.fields["preInfusionSeconds"])
        assertEquals(4L, document.fields["rating"])
        assertEquals(93.5, document.fields["brewTemperatureCelsius"])
        assertEquals(1_000L, document.fields["shotAt"])
        assertEquals(false, document.fields["isDeleted"])
        assertEquals(REMOTE_DOCUMENT_VERSION, document.fields["docVersion"])
    }

    @Test
    fun `shot log maps nulls explicitly`() {
        val document = shotLog().copy(
            grindSetting = null,
            extractionTimeSeconds = null,
            brewTemperatureCelsius = null,
            preInfusionSeconds = null,
            rating = null,
            notes = null,
        ).toRemoteDocument()

        assertTrue(document.fields.containsKey("grindSetting"))
        assertTrue(document.fields.containsKey("extractionTimeSeconds"))
        assertNull(document.fields["grindSetting"])
        assertNull(document.fields["extractionTimeSeconds"])
        assertNull(document.fields["rating"])
    }

    @Test
    fun `filter basket maps the size class`() {
        val document = FilterBasketEntity(
            id = "basket-1",
            brand = "IMS",
            model = "Competition",
            sizeGrams = "18g",
            sizeClass = "Double",
            type = "Precision",
            diameter = "58mm",
            purchaseDate = null,
            imageUri = null,
            notes = null,
            isDeleted = false,
            createdAt = 1_000L,
            updatedAt = 2_000L,
        ).toRemoteDocument()

        assertEquals(SyncCollection.FILTER_BASKETS, document.collection)
        assertEquals("basket-1", document.id)
        assertEquals("Double", document.fields["sizeClass"])
        assertEquals("18g", document.fields["sizeGrams"])
        assertEquals(2_000L, document.fields["updatedAt"])
    }

    private fun shotLog() = ShotLogEntity(
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
}
