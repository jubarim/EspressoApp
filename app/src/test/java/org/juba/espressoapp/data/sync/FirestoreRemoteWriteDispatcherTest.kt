package org.juba.espressoapp.data.sync

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.juba.espressoapp.data.remote.RemoteDocument
import org.juba.espressoapp.data.remote.SyncCollection
import org.juba.espressoapp.domain.model.AuthUser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class FirestoreRemoteWriteDispatcherTest {

    private val document = RemoteDocument(
        collection = SyncCollection.ROASTERS,
        id = "roaster-1",
        fields = mapOf("id" to "roaster-1"),
    )

    @Test
    fun `pushes for the signed-in user`() = runTest {
        val syncTarget = FakeSyncTarget()
        val dispatcher = FirestoreRemoteWriteDispatcher(
            scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler)),
            authRepository = FakeAuthRepository(AuthUser(uid = "uid-1", displayName = null, email = null)),
            syncTarget = syncTarget,
        )

        dispatcher.dispatch(document)

        val pushed = syncTarget.pushed.single()
        assertEquals("uid-1", pushed.uid)
        assertEquals(document, pushed.documents.single())
    }

    @Test
    fun `skips when there is no signed-in user`() = runTest {
        val syncTarget = FakeSyncTarget()
        val dispatcher = FirestoreRemoteWriteDispatcher(
            scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler)),
            authRepository = FakeAuthRepository(user = null),
            syncTarget = syncTarget,
        )

        dispatcher.dispatch(document)

        assertTrue(syncTarget.pushed.isEmpty())
    }
}
