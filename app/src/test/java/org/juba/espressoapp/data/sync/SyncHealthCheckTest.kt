package org.juba.espressoapp.data.sync

import kotlinx.coroutines.test.runTest
import org.juba.espressoapp.data.remote.SyncCollection
import org.juba.espressoapp.domain.model.AuthUser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SyncHealthCheckTest {

    @Test
    fun `fails when no user is signed in`() = runTest {
        val check = SyncHealthCheck(
            authRepository = FakeAuthRepository(user = null),
            syncTarget = FakeSyncTarget(),
            remoteSource = FakeRemoteSource(),
        )

        assertTrue(check.run().isFailure)
    }

    @Test
    fun `pushes a tombstoned document and waits for it to come back`() = runTest {
        val syncTarget = FakeSyncTarget()
        val check = SyncHealthCheck(
            authRepository = FakeAuthRepository(AuthUser(uid = "uid-1", displayName = null, email = null)),
            syncTarget = syncTarget,
            remoteSource = FakeRemoteSource(),
        )

        assertTrue(check.run().isSuccess)

        val pushed = syncTarget.pushed.single()
        assertEquals("uid-1", pushed.uid)
        val document = pushed.documents.single()
        assertEquals(SyncCollection.ROASTERS, document.collection)
        assertEquals(true, document.fields["isDeleted"])
    }
}
