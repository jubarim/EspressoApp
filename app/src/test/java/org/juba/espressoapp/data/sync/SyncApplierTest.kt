package org.juba.espressoapp.data.sync

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.test.runTest
import org.juba.espressoapp.data.local.database.AppDatabase
import org.juba.espressoapp.data.local.entity.CoffeeBeanEntity
import org.juba.espressoapp.data.local.entity.EspressoMachineEntity
import org.juba.espressoapp.data.local.entity.FilterBasketEntity
import org.juba.espressoapp.data.local.entity.GrinderEntity
import org.juba.espressoapp.data.local.entity.RoasterEntity
import org.juba.espressoapp.data.local.entity.ShotLogEntity
import org.juba.espressoapp.data.remote.RemoteChange
import org.juba.espressoapp.data.remote.RemoteChangeType
import org.juba.espressoapp.data.remote.RemoteDocument
import org.juba.espressoapp.data.remote.toRemoteDocument
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SyncApplierTest {

    private lateinit var db: AppDatabase
    private lateinit var applier: SyncApplier

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
        ).allowMainThreadQueries().build()
        applier = SyncApplier(
            roasterDao = db.roasterDao(),
            coffeeBeanDao = db.coffeeBeanDao(),
            grinderDao = db.grinderDao(),
            espressoMachineDao = db.espressoMachineDao(),
            filterBasketDao = db.filterBasketDao(),
            shotLogDao = db.shotLogDao(),
        )
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun `applies a remote record newer than the local one`() = runTest {
        db.roasterDao().insert(roaster(name = "Local", updatedAt = 100L))

        applier.apply(added(roaster(name = "Remote", updatedAt = 200L).toRemoteDocument()))

        val stored = db.roasterDao().getByIdIncludingDeleted(ROASTER_ID)
        assertEquals("Remote", stored?.name)
        assertEquals(200L, stored?.updatedAt)
    }

    @Test
    fun `keeps the local record when it is newer or equal`() = runTest {
        db.roasterDao().insert(roaster(name = "Local", updatedAt = 300L))

        applier.apply(added(roaster(name = "Remote", updatedAt = 200L).toRemoteDocument()))
        applier.apply(added(roaster(name = "Remote", updatedAt = 300L).toRemoteDocument()))

        assertEquals("Local", db.roasterDao().getByIdIncludingDeleted(ROASTER_ID)?.name)
    }

    @Test
    fun `applies a remote tombstone`() = runTest {
        db.roasterDao().insert(roaster(updatedAt = 100L))

        applier.apply(added(roaster(updatedAt = 200L, isDeleted = true).toRemoteDocument()))

        val stored = db.roasterDao().getByIdIncludingDeleted(ROASTER_ID)
        assertTrue(stored?.isDeleted == true)
    }

    @Test
    fun `ignores remote removals`() = runTest {
        db.roasterDao().insert(roaster(name = "Local", updatedAt = 100L))

        applier.apply(
            RemoteChange(
                document = roaster(name = "Remote", updatedAt = 200L).toRemoteDocument(),
                type = RemoteChangeType.Removed,
            ),
        )

        assertEquals("Local", db.roasterDao().getByIdIncludingDeleted(ROASTER_ID)?.name)
    }

    @Test
    fun `waits for missing parents before inserting a shot`() = runTest {
        applier.apply(added(shotLog().toRemoteDocument()))
        assertNull(db.shotLogDao().getByIdIncludingDeleted(SHOT_ID))

        applier.apply(added(roaster().toRemoteDocument()))
        applier.apply(added(coffeeBean().toRemoteDocument()))
        applier.apply(added(grinder().toRemoteDocument()))
        applier.apply(added(espressoMachine().toRemoteDocument()))
        applier.apply(added(filterBasket().toRemoteDocument()))

        assertNotNull(db.shotLogDao().getByIdIncludingDeleted(SHOT_ID))
    }

    private fun added(document: RemoteDocument) = RemoteChange(document, RemoteChangeType.Added)

    private fun roaster(
        name: String = "Roaster",
        updatedAt: Long = 100L,
        isDeleted: Boolean = false,
    ) = RoasterEntity(
        id = ROASTER_ID,
        name = name,
        country = null,
        city = null,
        website = null,
        imageUri = null,
        notes = null,
        isDeleted = isDeleted,
        createdAt = 100L,
        updatedAt = updatedAt,
    )

    private fun coffeeBean(updatedAt: Long = 100L) = CoffeeBeanEntity(
        id = BEAN_ID,
        roasterId = ROASTER_ID,
        name = "Bean",
        origin = null,
        process = null,
        roastLevel = null,
        roastDate = null,
        imageUri = null,
        notes = null,
        isDeleted = false,
        createdAt = 100L,
        updatedAt = updatedAt,
    )

    private fun grinder(updatedAt: Long = 100L) = GrinderEntity(
        id = GRINDER_ID,
        brand = "Niche",
        model = "Zero",
        burrType = null,
        burrSize = null,
        purchaseDate = null,
        burrInstallDate = null,
        imageUri = null,
        notes = null,
        isDeleted = false,
        createdAt = 100L,
        updatedAt = updatedAt,
    )

    private fun espressoMachine(updatedAt: Long = 100L) = EspressoMachineEntity(
        id = MACHINE_ID,
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
        createdAt = 100L,
        updatedAt = updatedAt,
    )

    private fun filterBasket(updatedAt: Long = 100L) = FilterBasketEntity(
        id = BASKET_ID,
        brand = "IMS",
        model = null,
        sizeGrams = null,
        sizeClass = null,
        type = null,
        diameter = null,
        purchaseDate = null,
        imageUri = null,
        notes = null,
        isDeleted = false,
        createdAt = 100L,
        updatedAt = updatedAt,
    )

    private fun shotLog(updatedAt: Long = 100L) = ShotLogEntity(
        id = SHOT_ID,
        coffeeBeanId = BEAN_ID,
        grinderId = GRINDER_ID,
        machineId = MACHINE_ID,
        basketId = BASKET_ID,
        grindSetting = null,
        doseGrams = 18.0,
        yieldGrams = 36.0,
        extractionTimeSeconds = null,
        brewTemperatureCelsius = null,
        preInfusionSeconds = null,
        rating = null,
        notes = null,
        shotAt = 100L,
        isDeleted = false,
        createdAt = 100L,
        updatedAt = updatedAt,
    )

    private companion object {
        const val ROASTER_ID = "roaster-1"
        const val BEAN_ID = "bean-1"
        const val GRINDER_ID = "grinder-1"
        const val MACHINE_ID = "machine-1"
        const val BASKET_ID = "basket-1"
        const val SHOT_ID = "shot-1"
    }
}
