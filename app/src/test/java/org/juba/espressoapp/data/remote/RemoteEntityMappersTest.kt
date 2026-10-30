package org.juba.espressoapp.data.remote

import org.juba.espressoapp.data.local.entity.FilterBasketEntity
import org.juba.espressoapp.data.local.entity.ShotLogEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RemoteEntityMappersTest {

    @Test
    fun `shot log survives a round trip`() {
        val entity = shotLog()

        assertEquals(entity, entity.toRemoteDocument().toShotLogEntity())
    }

    @Test
    fun `filter basket survives a round trip`() {
        val entity = filterBasket()

        assertEquals(entity, entity.toRemoteDocument().toFilterBasketEntity())
    }

    @Test
    fun `missing required fields are rejected`() {
        val document = RemoteDocument(
            collection = SyncCollection.ROASTERS,
            id = "roaster-1",
            fields = mapOf("updatedAt" to 1_000L),
        )

        assertNull(document.toRoasterEntity())
    }

    @Test
    fun `wrong field types are rejected`() {
        val document = RemoteDocument(
            collection = SyncCollection.ROASTERS,
            id = "roaster-1",
            fields = mapOf("name" to 42, "updatedAt" to "yesterday"),
        )

        assertNull(document.toRoasterEntity())
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

    private fun filterBasket() = FilterBasketEntity(
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
    )
}
