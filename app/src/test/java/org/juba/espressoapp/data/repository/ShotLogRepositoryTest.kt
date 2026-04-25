package org.juba.espressoapp.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.juba.espressoapp.data.local.database.AppDatabase
import org.juba.espressoapp.data.local.entity.CoffeeBeanEntity
import org.juba.espressoapp.data.local.entity.EspressoMachineEntity
import org.juba.espressoapp.data.local.entity.FilterBasketEntity
import org.juba.espressoapp.data.local.entity.GrinderEntity
import org.juba.espressoapp.data.local.entity.RoasterEntity
import org.juba.espressoapp.domain.model.ShotLog
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ShotLogRepositoryTest {

    private lateinit var db: AppDatabase
    private lateinit var repository: ShotLogRepositoryImpl

    @Before
    fun setup() = runTest {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
        ).allowMainThreadQueries().build()
        repository = ShotLogRepositoryImpl(db.shotLogDao())
        insertPrerequisites()
    }

    @After
    fun teardown() {
        db.close()
    }

    private suspend fun insertPrerequisites() {
        db.roasterDao().insert(
            RoasterEntity(
                id = "roaster-1",
                name = "Square Mile",
                country = null,
                city = null,
                website = null,
                imageUri = null,
                notes = null,
                isDeleted = false,
                createdAt = 1_000L,
                updatedAt = 1_000L,
            ),
        )
        db.coffeeBeanDao().insert(
            CoffeeBeanEntity(
                id = "bean-1",
                roasterId = "roaster-1",
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
            ),
        )
        db.grinderDao().insert(
            GrinderEntity(
                id = "grinder-1",
                brand = "Niche",
                model = "Zero",
                burrType = null,
                burrSize = null,
                purchaseDate = null,
                burrInstallDate = null,
                imageUri = null,
                notes = null,
                isDeleted = false,
                createdAt = 1_000L,
                updatedAt = 1_000L,
            ),
        )
        db.espressoMachineDao().insert(
            EspressoMachineEntity(
                id = "machine-1",
                brand = "ECM",
                model = "Synchronika",
                boilerType = null,
                pumpType = null,
                groupHead = null,
                hasPressureGauge = false,
                purchaseDate = null,
                imageUri = null,
                notes = null,
                isDeleted = false,
                createdAt = 1_000L,
                updatedAt = 1_000L,
            ),
        )
        db.filterBasketDao().insert(
            FilterBasketEntity(
                id = "basket-1",
                brand = "IMS",
                model = "Precision",
                sizeGrams = null,
                type = null,
                diameter = null,
                purchaseDate = null,
                imageUri = null,
                notes = null,
                isDeleted = false,
                createdAt = 1_000L,
                updatedAt = 1_000L,
            ),
        )
    }

    private fun shot(id: String = "shot-1") = ShotLog(
        id = id,
        coffeeBeanId = "bean-1",
        coffeeBeanName = null,
        roasterName = null,
        grinderId = "grinder-1",
        grinderName = null,
        machineId = "machine-1",
        machineName = null,
        basketId = "basket-1",
        basketName = null,
        grindSetting = "3.2",
        doseGrams = 18.0,
        yieldGrams = 36.0,
        ratio = 2.0,
        extractionTimeSeconds = 27,
        brewTemperatureCelsius = null,
        preInfusionSeconds = null,
        rating = 4,
        notes = null,
        shotAt = 1_000L,
        createdAt = 1_000L,
        updatedAt = 1_000L,
    )

    @Test
    fun `insert and getAll returns inserted shot`() = runTest {
        repository.insert(shot())
        val result = repository.getAll().first()
        assertEquals(1, result.size)
        assertEquals("shot-1", result[0].id)
        assertEquals(18.0, result[0].doseGrams, 0.0)
        assertEquals(36.0, result[0].yieldGrams, 0.0)
    }

    @Test
    fun `ratio is computed correctly after insert`() = runTest {
        repository.insert(shot())
        val result = repository.getAll().first()
        assertEquals(2.0, result[0].ratio, 0.0001)
    }

    @Test
    fun `getById returns correct shot`() = runTest {
        repository.insert(shot())
        val result = repository.getById("shot-1")
        assertEquals("shot-1", result?.id)
        assertEquals(4, result?.rating)
    }

    @Test
    fun `getById returns null for unknown id`() = runTest {
        assertNull(repository.getById("unknown"))
    }

    @Test
    fun `update persists changes`() = runTest {
        repository.insert(shot())
        repository.update(shot().copy(rating = 5, notes = "God shot"))
        val result = repository.getById("shot-1")
        assertEquals(5, result?.rating)
        assertEquals("God shot", result?.notes)
    }

    @Test
    fun `delete soft-deletes and hides from getAll`() = runTest {
        repository.insert(shot())
        repository.delete("shot-1")
        assertTrue(repository.getAll().first().isEmpty())
    }

    @Test
    fun `getById returns null after soft delete`() = runTest {
        repository.insert(shot())
        repository.delete("shot-1")
        assertNull(repository.getById("shot-1"))
    }

    @Test
    fun `insert returns failure on duplicate id`() = runTest {
        repository.insert(shot())
        val result = repository.insert(shot())
        assertTrue(result.isFailure)
    }

    @Test
    fun `getAll returns shots sorted by shot_at descending`() = runTest {
        repository.insert(shot(id = "shot-1").copy(shotAt = 1_000L, updatedAt = 1_000L))
        repository.insert(shot(id = "shot-2").copy(shotAt = 3_000L, updatedAt = 3_000L))
        repository.insert(shot(id = "shot-3").copy(shotAt = 2_000L, updatedAt = 2_000L))
        val result = repository.getAll().first()
        assertEquals(listOf("shot-2", "shot-3", "shot-1"), result.map { it.id })
    }
}
