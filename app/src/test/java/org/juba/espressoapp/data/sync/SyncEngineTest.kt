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
import org.juba.espressoapp.data.remote.SyncCollection
import org.juba.espressoapp.domain.model.AuthUser
import org.junit.After
import org.junit.Assert.assertEquals
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
    fun `attaches one listener per collection for the signed-in user`() = runTest {
        val auth = MutableFakeAuthRepository(AuthUser(uid = "uid-1", displayName = null, email = null))
        val remoteSource = TrackingRemoteSource()
        val engine = engine(auth, remoteSource)

        engine.start()
        advanceUntilIdle()

        assertEquals(SyncCollection.entries, remoteSource.observations.map { it.collection })
        assertTrue(remoteSource.observations.all { it.uid == "uid-1" })

        engine.stop()
    }

    @Test
    fun `does not attach listeners without a user`() = runTest {
        val remoteSource = TrackingRemoteSource()
        val engine = engine(MutableFakeAuthRepository(null), remoteSource)

        engine.start()
        advanceUntilIdle()

        assertTrue(remoteSource.observations.isEmpty())

        engine.stop()
    }

    @Test
    fun `detaches listeners when the user signs out`() = runTest {
        val auth = MutableFakeAuthRepository(AuthUser(uid = "uid-1", displayName = null, email = null))
        val remoteSource = TrackingRemoteSource()
        val engine = engine(auth, remoteSource)

        engine.start()
        advanceUntilIdle()
        assertEquals(SyncCollection.entries.size, remoteSource.observations.size)

        auth.user.value = null
        advanceUntilIdle()

        assertTrue(remoteSource.observations.all { it.cancelled })

        engine.stop()
    }

    private fun TestScope.engine(
        auth: MutableFakeAuthRepository,
        remoteSource: TrackingRemoteSource,
    ) = SyncEngine(
        scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler)),
        authRepository = auth,
        remoteSource = remoteSource,
        applier = applier,
    )
}
