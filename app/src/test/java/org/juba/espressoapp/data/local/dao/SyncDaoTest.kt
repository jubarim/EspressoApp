package org.juba.espressoapp.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.juba.espressoapp.data.local.database.AppDatabase
import org.juba.espressoapp.data.local.entity.CoffeeBeanEntity
import org.juba.espressoapp.data.local.entity.RoasterEntity
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SyncDaoTest {

    private lateinit var db: AppDatabase

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
        ).allowMainThreadQueries().build()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun `getAllIncludingDeleted returns soft deleted rows`() = runTest {
        val dao = db.roasterDao()
        dao.insert(roaster(id = "roaster-1"))
        dao.softDelete(id = "roaster-1", timestamp = 2_000L)

        assertTrue(dao.getAll().first().isEmpty())
        val all = dao.getAllIncludingDeleted()
        assertEquals(listOf("roaster-1"), all.map { it.id })
        assertTrue(all.single().isDeleted)
    }

    @Test
    fun `upsert updates a parent that still has children`() = runTest {
        db.roasterDao().insert(roaster(id = "roaster-1"))
        db.coffeeBeanDao().insert(coffeeBean(id = "bean-1", roasterId = "roaster-1"))

        db.roasterDao().upsert(roaster(id = "roaster-1", name = "Renamed"))

        assertEquals("Renamed", db.roasterDao().getById("roaster-1")?.name)
        assertEquals(1, db.roasterDao().getAllIncludingDeleted().size)
    }

    private fun roaster(id: String, name: String = "Square Mile") = RoasterEntity(
        id = id,
        name = name,
        country = null,
        city = null,
        website = null,
        imageUri = null,
        notes = null,
        isDeleted = false,
        createdAt = 1_000L,
        updatedAt = 1_000L,
    )

    private fun coffeeBean(id: String, roasterId: String) = CoffeeBeanEntity(
        id = id,
        roasterId = roasterId,
        name = "Ethiopia Yirgacheffe",
        origin = null,
        process = null,
        roastLevel = null,
        roastDate = null,
        imageUri = null,
        notes = null,
        isDeleted = false,
        createdAt = 1_000L,
        updatedAt = 1_000L,
    )
}
