package org.juba.espressoapp.data.local.mapper

import org.juba.espressoapp.data.local.entity.RoasterEntity
import org.juba.espressoapp.domain.model.Roaster
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RoasterMapperTest {

    private val entity = RoasterEntity(
        id = "abc-123",
        name = "Square Mile",
        country = "UK",
        website = "https://squaremilecoffee.com",
        imageUri = null,
        notes = "Great light roasts",
        isDeleted = false,
        createdAt = 1_000L,
        updatedAt = 2_000L,
    )

    private val domain = Roaster(
        id = "abc-123",
        name = "Square Mile",
        country = "UK",
        website = "https://squaremilecoffee.com",
        imageUri = null,
        notes = "Great light roasts",
        createdAt = 1_000L,
        updatedAt = 2_000L,
    )

    @Test
    fun `entity toDomain maps all fields`() {
        val result = entity.toDomain()
        assertEquals(domain, result)
    }

    @Test
    fun `domain toEntity maps all fields and sets isDeleted false`() {
        val result = domain.toEntity()
        assertEquals(entity, result)
        assertEquals(false, result.isDeleted)
    }

    @Test
    fun `nullable fields survive round trip`() {
        val sparse = entity.copy(country = null, website = null, notes = null, imageUri = null)
        val result = sparse.toDomain()
        assertNull(result.country)
        assertNull(result.website)
        assertNull(result.notes)
        assertNull(result.imageUri)
    }
}
