package org.juba.espressoapp.data.sync

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.juba.espressoapp.data.remote.RemoteChange
import org.juba.espressoapp.data.remote.RemoteChangeType
import org.juba.espressoapp.data.remote.RemoteDocument
import org.juba.espressoapp.data.remote.RemoteSource
import org.juba.espressoapp.data.remote.SyncCollection
import org.juba.espressoapp.data.remote.SyncTarget
import org.juba.espressoapp.domain.model.AuthUser
import org.juba.espressoapp.domain.repository.AuthRepository

internal class FakeAuthRepository(private val user: AuthUser?) : AuthRepository {
    override fun currentUser(): Flow<AuthUser?> = flowOf(user)
    override suspend fun signInWithGoogle(idToken: String): Result<Unit> = Result.success(Unit)
    override suspend fun signOut() = Unit
}

internal class FakeSyncTarget : SyncTarget {
    data class Pushed(val uid: String, val documents: List<RemoteDocument>)

    val pushed = mutableListOf<Pushed>()

    override suspend fun push(uid: String, documents: List<RemoteDocument>): Result<Unit> {
        pushed += Pushed(uid, documents)
        return Result.success(Unit)
    }

    override suspend fun awaitPendingWrites(): Result<Unit> = Result.success(Unit)
}

internal class FakeRemoteSource(
    private val documentId: String = "sync-health-check",
) : RemoteSource {
    override fun observe(uid: String, collection: SyncCollection): Flow<RemoteChange> = flowOf(
        RemoteChange(
            document = RemoteDocument(
                collection = collection,
                id = documentId,
                fields = emptyMap(),
            ),
            type = RemoteChangeType.Added,
        ),
    )
}

/** Records dispatched documents without touching the network. */
internal class RecordingRemoteWriteDispatcher : RemoteWriteDispatcher {
    val documents = mutableListOf<RemoteDocument>()

    override fun dispatch(document: RemoteDocument) {
        documents += document
    }
}
