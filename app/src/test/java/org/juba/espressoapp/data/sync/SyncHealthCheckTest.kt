package org.juba.espressoapp.data.sync

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.juba.espressoapp.data.remote.RemoteChange
import org.juba.espressoapp.data.remote.RemoteChangeType
import org.juba.espressoapp.data.remote.RemoteDocument
import org.juba.espressoapp.data.remote.RemoteSource
import org.juba.espressoapp.data.remote.SyncCollection
import org.juba.espressoapp.data.remote.SyncTarget
import org.juba.espressoapp.domain.model.AuthUser
import org.juba.espressoapp.domain.repository.AuthRepository
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

private class FakeAuthRepository(private val user: AuthUser?) : AuthRepository {
    override fun currentUser(): Flow<AuthUser?> = flowOf(user)
    override suspend fun signInWithGoogle(idToken: String): Result<Unit> = Result.success(Unit)
    override suspend fun signOut() = Unit
}

private class FakeSyncTarget : SyncTarget {
    data class Pushed(val uid: String, val documents: List<RemoteDocument>)

    val pushed = mutableListOf<Pushed>()

    override suspend fun push(uid: String, documents: List<RemoteDocument>): Result<Unit> {
        pushed += Pushed(uid, documents)
        return Result.success(Unit)
    }

    override suspend fun awaitPendingWrites(): Result<Unit> = Result.success(Unit)
}

private class FakeRemoteSource : RemoteSource {
    override fun observe(uid: String, collection: SyncCollection): Flow<RemoteChange> = flowOf(
        RemoteChange(
            document = RemoteDocument(
                collection = collection,
                id = "sync-health-check",
                fields = emptyMap(),
            ),
            type = RemoteChangeType.Added,
        ),
    )
}
