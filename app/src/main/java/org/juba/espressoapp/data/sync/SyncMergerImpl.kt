package org.juba.espressoapp.data.sync

import android.os.SystemClock
import org.juba.espressoapp.data.local.dao.CoffeeBeanDao
import org.juba.espressoapp.data.local.dao.EspressoMachineDao
import org.juba.espressoapp.data.local.dao.FilterBasketDao
import org.juba.espressoapp.data.local.dao.GrinderDao
import org.juba.espressoapp.data.local.dao.RoasterDao
import org.juba.espressoapp.data.local.dao.ShotLogDao
import org.juba.espressoapp.data.remote.RemoteDocument
import org.juba.espressoapp.data.remote.RemoteSource
import org.juba.espressoapp.data.remote.SyncCollection
import org.juba.espressoapp.data.remote.SyncTarget
import org.juba.espressoapp.data.remote.toRemoteDocument
import org.juba.espressoapp.domain.repository.PreferencesRepository
import org.juba.espressoapp.logging.AppLog
import javax.inject.Inject

private const val TAG = "EspressoSync"

/** [SyncMerger] that compares Room with the remote collections read once per start. */
class SyncMergerImpl @Inject constructor(
    private val roasterDao: RoasterDao,
    private val coffeeBeanDao: CoffeeBeanDao,
    private val grinderDao: GrinderDao,
    private val espressoMachineDao: EspressoMachineDao,
    private val filterBasketDao: FilterBasketDao,
    private val shotLogDao: ShotLogDao,
    private val remoteSource: RemoteSource,
    private val syncTarget: SyncTarget,
    private val preferencesRepository: PreferencesRepository,
) : SyncMerger {

    override suspend fun merge(uid: String): Result<Unit> = runCatching {
        val startedAt = SystemClock.elapsedRealtime()
        val toPush = mutableListOf<RemoteDocument>()

        for (collection in SyncCollection.entries) {
            val remoteUpdatedAt = remoteSource.fetchAll(uid, collection)
                .associate { it.id to it.updatedAt }
            toPush += localRecords(collection)
                .filter { record ->
                    val remote = remoteUpdatedAt[record.id]
                    remote == null || record.updatedAt > remote
                }
                .map { it.document }
        }

        if (toPush.isEmpty()) {
            preferencesRepository.setLastSyncAt(System.currentTimeMillis())
            AppLog.d(TAG) { "merge uid=$uid, nothing to push" }
            return@runCatching
        }

        syncTarget.push(uid, toPush).getOrThrow()
        preferencesRepository.setLastSyncAt(System.currentTimeMillis())
        AppLog.d(TAG) {
            "merge uid=$uid pushed=${toPush.size} in ${SystemClock.elapsedRealtime() - startedAt} ms"
        }
    }

    private suspend fun localRecords(collection: SyncCollection): List<LocalRecord> =
        when (collection) {
            SyncCollection.ROASTERS -> roasterDao.getAllIncludingDeleted()
                .map { LocalRecord(it.id, it.updatedAt, it.toRemoteDocument()) }

            SyncCollection.COFFEE_BEANS -> coffeeBeanDao.getAllIncludingDeleted()
                .map { LocalRecord(it.id, it.updatedAt, it.toRemoteDocument()) }

            SyncCollection.GRINDERS -> grinderDao.getAllIncludingDeleted()
                .map { LocalRecord(it.id, it.updatedAt, it.toRemoteDocument()) }

            SyncCollection.ESPRESSO_MACHINES -> espressoMachineDao.getAllIncludingDeleted()
                .map { LocalRecord(it.id, it.updatedAt, it.toRemoteDocument()) }

            SyncCollection.FILTER_BASKETS -> filterBasketDao.getAllIncludingDeleted()
                .map { LocalRecord(it.id, it.updatedAt, it.toRemoteDocument()) }

            SyncCollection.SHOT_LOGS -> shotLogDao.getAllIncludingDeleted()
                .map { LocalRecord(it.id, it.updatedAt, it.toRemoteDocument()) }
        }
}

private data class LocalRecord(
    val id: String,
    val updatedAt: Long,
    val document: RemoteDocument,
)

private val RemoteDocument.updatedAt: Long? get() = (fields["updatedAt"] as? Number)?.toLong()
