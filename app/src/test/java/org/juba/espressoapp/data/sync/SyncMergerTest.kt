package org.juba.espressoapp.data.sync

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.test.runTest
import org.juba.espressoapp.data.local.database.AppDatabase
import org.juba.espressoapp.data.local.entity.RoasterEntity
import org.juba.espressoapp.data.remote.RemoteDocument
import org.juba.espressoapp.data.remote.toRemoteDocument
import org.juba.espressoapp.data.remote.SyncCollection
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SyncMergerTest {

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
    fun `pushes local records missing from the cloud`() = runTest {
        db.roasterDao().insert(roaster(updatedAt = 100L))
        val syncTarget = FakeSyncTarget()
        val merger = merger(syncTarget = syncTarget)

        assertTrue(merger.merge(UID).isSuccess)

        val document = syncTarget.pushed.single().documents.single()
        assertEquals(SyncCollection.ROASTERS, document.collection)
        assertEquals("roaster-1", document.id)
    }

    @Test
    fun `pushes local records newer than the cloud copy`() = runTest {
        db.roasterDao().insert(roaster(updatedAt = 300L))
        val syncTarget = FakeSyncTarget()
        val merger = merger(syncTarget = syncTarget, remote = listOf(roaster(updatedAt = 100L)))

        assertTrue(merger.merge(UID).isSuccess)

        assertEquals("roaster-1", syncTarget.pushed.single().documents.single().id)
    }

    @Test
    fun `skips local records the cloud already has at the same version`() = runTest {
        db.roasterDao().insert(roaster(updatedAt = 100L))
        val syncTarget = FakeSyncTarget()
        val merger = merger(syncTarget = syncTarget, remote = listOf(roaster(updatedAt = 100L)))

        assertTrue(merger.merge(UID).isSuccess)

        assertTrue(syncTarget.pushed.isEmpty())
    }

    @Test
    fun `skips local records the cloud already has at a newer version`() = runTest {
        db.roasterDao().insert(roaster(updatedAt = 100L))
        val syncTarget = FakeSyncTarget()
        val merger = merger(syncTarget = syncTarget, remote = listOf(roaster(updatedAt = 300L)))

        assertTrue(merger.merge(UID).isSuccess)

        assertTrue(syncTarget.pushed.isEmpty())
    }

    @Test
    fun `pushes tombstones`() = runTest {
        db.roasterDao().insert(roaster(updatedAt = 300L, isDeleted = true))
        val syncTarget = FakeSyncTarget()
        val merger = merger(syncTarget = syncTarget, remote = listOf(roaster(updatedAt = 100L)))

        assertTrue(merger.merge(UID).isSuccess)

        val document = syncTarget.pushed.single().documents.single()
        assertEquals(true, document.fields["isDeleted"])
    }

    @Test
    fun `records the last sync time only when something was pushed`() = runTest {
        val emptyPreferences = FakePreferencesRepository()
        val emptyMerger = merger(syncTarget = FakeSyncTarget(), preferences = emptyPreferences)
        emptyMerger.merge(UID)
        assertNull(emptyPreferences.lastSyncAtValue)

        db.roasterDao().insert(roaster(updatedAt = 100L))
        val preferences = FakePreferencesRepository()
        val merger = merger(syncTarget = FakeSyncTarget(), preferences = preferences)
        merger.merge(UID)
        assertTrue(preferences.lastSyncAtValue != null)
    }

    private fun merger(
        syncTarget: FakeSyncTarget,
        remote: List<RoasterEntity> = emptyList(),
        preferences: FakePreferencesRepository = FakePreferencesRepository(),
    ) = SyncMergerImpl(
        roasterDao = db.roasterDao(),
        coffeeBeanDao = db.coffeeBeanDao(),
        grinderDao = db.grinderDao(),
        espressoMachineDao = db.espressoMachineDao(),
        filterBasketDao = db.filterBasketDao(),
        shotLogDao = db.shotLogDao(),
        remoteSource = FakeRemoteSource(remoteDocuments = remote.map { it.toRemoteDocument() }),
        syncTarget = syncTarget,
        preferencesRepository = preferences,
    )

    private fun roaster(updatedAt: Long, isDeleted: Boolean = false) = RoasterEntity(
        id = "roaster-1",
        name = "Roaster",
        country = null,
        city = null,
        website = null,
        imageUri = null,
        notes = null,
        isDeleted = isDeleted,
        createdAt = 100L,
        updatedAt = updatedAt,
    )

    private companion object {
        const val UID = "uid-1"
    }
}
