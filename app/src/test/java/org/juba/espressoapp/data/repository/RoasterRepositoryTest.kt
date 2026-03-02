package org.juba.espressoapp.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.juba.espressoapp.data.local.database.AppDatabase
import org.juba.espressoapp.domain.model.Roaster
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RoasterRepositoryTest {

    private lateinit var db: AppDatabase
    private lateinit var repository: RoasterRepositoryImpl

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
        ).allowMainThreadQueries().build()
        repository = RoasterRepositoryImpl(db.roasterDao())
    }

    @After
    fun teardown() {
        db.close()
    }

    private fun roaster(id: String = "id-1", name: String = "Test Roaster") = Roaster(
        id = id,
        name = name,
        country = "PT",
        website = null,
        imageUri = null,
        notes = null,
        createdAt = 1_000L,
        updatedAt = 1_000L,
    )

    @Test
    fun `insert and getAll returns inserted roaster`() = runTest {
        repository.insert(roaster())
        val result = repository.getAll().first()
        assertEquals(1, result.size)
        assertEquals("Test Roaster", result[0].name)
    }

    @Test
    fun `getById returns correct roaster`() = runTest {
        repository.insert(roaster())
        val result = repository.getById("id-1")
        assertEquals("Test Roaster", result?.name)
    }

    @Test
    fun `update persists changes`() = runTest {
        repository.insert(roaster())
        repository.update(roaster(name = "Updated Name"))
        val result = repository.getById("id-1")
        assertEquals("Updated Name", result?.name)
    }

    @Test
    fun `delete soft-deletes and hides from getAll`() = runTest {
        repository.insert(roaster())
        repository.delete("id-1")
        val result = repository.getAll().first()
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getById returns null after soft delete`() = runTest {
        repository.insert(roaster())
        repository.delete("id-1")
        assertNull(repository.getById("id-1"))
    }

    @Test
    fun `insert returns failure on duplicate id`() = runTest {
        repository.insert(roaster())
        val result = repository.insert(roaster())
        assertTrue(result.isFailure)
    }
}
