package org.juba.espressoapp.data.sync

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.juba.espressoapp.data.local.database.AppDatabase
import org.juba.espressoapp.data.local.entity.RoasterEntity
import org.juba.espressoapp.data.remote.SyncCollection
import org.juba.espressoapp.domain.model.AuthUser
import org.juba.espressoapp.domain.repository.AuthRepository
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class SyncEngineTest {

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
    fun `claims ownership, merges and attaches one listener per collection`() = runTest {
        val preferences = FakePreferencesRepository()
        val merger = FakeSyncMerger()
        val remoteSource = TrackingRemoteSource()
        val engine = engine(MutableFakeAuthRepository(user()), preferences, remoteSource, merger)

        engine.start()
        advanceUntilIdle()

        assertEquals("uid-1", preferences.dataOwnerUidValue)
        assertEquals(listOf("uid-1"), merger.mergedUids)
        assertEquals(SyncCollection.entries, remoteSource.observations.map { it.collection })
        assertTrue(remoteSource.observations.all { it.uid == "uid-1" })

        engine.stop()
    }

    @Test
    fun `does not attach listeners on the welcome screen`() = runTest {
        val preferences = FakePreferencesRepository()
        val merger = FakeSyncMerger()
        val remoteSource = TrackingRemoteSource()
        val engine = engine(MutableFakeAuthRepository(initialUser = null), preferences, remoteSource, merger)

        engine.start()
        advanceUntilIdle()

        assertTrue(remoteSource.observations.isEmpty())
        assertTrue(merger.mergedUids.isEmpty())
        assertNull(preferences.dataOwnerUidValue)

        engine.stop()
    }

    @Test
    fun `detaches listeners when the user signs out`() = runTest {
        val auth = MutableFakeAuthRepository(user())
        val remoteSource = TrackingRemoteSource()
        val engine = engine(auth, FakePreferencesRepository(), remoteSource, FakeSyncMerger())

        engine.start()
        advanceUntilIdle()
        assertEquals(SyncCollection.entries.size, remoteSource.observations.size)

        auth.user.value = null
        advanceUntilIdle()

        assertTrue(remoteSource.observations.all { it.cancelled })

        engine.stop()
    }

    @Test
    fun `sync now reattaches the listeners`() = runTest {
        val remoteSource = TrackingRemoteSource()
        val engine = engine(
            MutableFakeAuthRepository(user()),
            FakePreferencesRepository(),
            remoteSource,
            FakeSyncMerger(),
        )

        engine.start()
        advanceUntilIdle()
        assertEquals(SyncCollection.entries.size, remoteSource.observations.size)

        engine.syncNow()
        advanceUntilIdle()

        assertEquals(SyncCollection.entries.size * 2, remoteSource.observations.size)
        assertTrue(remoteSource.observations.take(SyncCollection.entries.size).all { it.cancelled })

        engine.stop()
    }

    @Test
    fun `merges again for the same owner without wiping`() = runTest {
        db.roasterDao().insert(roaster())
        val preferences = FakePreferencesRepository(ownerUid = "uid-1")
        val merger = FakeSyncMerger()
        val engine = engine(MutableFakeAuthRepository(user()), preferences, TrackingRemoteSource(), merger)

        engine.start()
        advanceUntilIdle()

        assertEquals(listOf("uid-1"), merger.mergedUids)
        assertEquals(1, db.roasterDao().getAllIncludingDeleted().size)

        engine.stop()
    }

    @Test
    fun `wipes local data when a different account signs in`() = runTest {
        db.roasterDao().insert(roaster())
        val preferences = FakePreferencesRepository(ownerUid = "old-uid")
        val merger = FakeSyncMerger()
        val engine = engine(MutableFakeAuthRepository(user()), preferences, TrackingRemoteSource(), merger)

        engine.start()
        advanceUntilIdle()

        assertTrue(db.roasterDao().getAllIncludingDeleted().isEmpty())
        assertEquals("uid-1", preferences.dataOwnerUidValue)
        assertTrue(merger.mergedUids.isEmpty())

        engine.stop()
    }

    @Test
    fun `wipes local data when a guest session starts after an account`() = runTest {
        db.roasterDao().insert(roaster())
        val preferences = FakePreferencesRepository(onboardingComplete = true, ownerUid = "old-uid")
        val remoteSource = TrackingRemoteSource()
        val engine = engine(
            MutableFakeAuthRepository(initialUser = null),
            preferences,
            remoteSource,
            FakeSyncMerger(),
        )

        engine.start()
        advanceUntilIdle()

        assertTrue(db.roasterDao().getAllIncludingDeleted().isEmpty())
        assertNull(preferences.dataOwnerUidValue)
        assertTrue(remoteSource.observations.isEmpty())

        engine.stop()
    }

    private fun TestScope.engine(
        auth: AuthRepository,
        preferences: FakePreferencesRepository,
        remoteSource: TrackingRemoteSource,
        merger: FakeSyncMerger,
    ) = SyncEngine(
        scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler)),
        authRepository = auth,
        preferencesRepository = preferences,
        remoteSource = remoteSource,
        syncTarget = FakeSyncTarget(),
        applier = applier,
        merger = merger,
        database = db,
    )

    private fun user() = AuthUser(uid = "uid-1", displayName = null, email = null)

    private fun roaster() = RoasterEntity(
        id = "roaster-1",
        name = "Roaster",
        country = null,
        city = null,
        website = null,
        imageUri = null,
        notes = null,
        isDeleted = false,
        createdAt = 100L,
        updatedAt = 100L,
    )
}
