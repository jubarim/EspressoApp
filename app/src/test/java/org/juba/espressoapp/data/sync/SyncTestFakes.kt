package org.juba.espressoapp.data.sync

import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
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

internal class MutableFakeAuthRepository(initialUser: AuthUser?) : AuthRepository {
    val user = MutableStateFlow(initialUser)

    override fun currentUser(): Flow<AuthUser?> = user
    override suspend fun signInWithGoogle(idToken: String): Result<Unit> = Result.success(Unit)
    override suspend fun signOut() {
        user.value = null
    }
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

/** Remote source that never emits and records when each listener is attached and cancelled. */
internal class TrackingRemoteSource : RemoteSource {
    class Observation(val uid: String, val collection: SyncCollection) {
        var cancelled = false
    }

    val observations = mutableListOf<Observation>()

    override fun observe(uid: String, collection: SyncCollection): Flow<RemoteChange> = flow {
        val observation = Observation(uid, collection)
        observations += observation
        try {
            awaitCancellation()
        } finally {
            observation.cancelled = true
        }
    }
}

/** Records dispatched documents without touching the network. */
internal class RecordingRemoteWriteDispatcher : RemoteWriteDispatcher {
    val documents = mutableListOf<RemoteDocument>()

    override fun dispatch(document: RemoteDocument) {
        documents += document
    }
}
