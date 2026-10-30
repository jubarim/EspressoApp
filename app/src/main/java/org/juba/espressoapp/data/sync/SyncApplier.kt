package org.juba.espressoapp.data.sync

import android.database.sqlite.SQLiteConstraintException
import android.os.SystemClock
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.juba.espressoapp.data.local.dao.CoffeeBeanDao
import org.juba.espressoapp.data.local.dao.EspressoMachineDao
import org.juba.espressoapp.data.local.dao.FilterBasketDao
import org.juba.espressoapp.data.local.dao.GrinderDao
import org.juba.espressoapp.data.local.dao.RoasterDao
import org.juba.espressoapp.data.local.dao.ShotLogDao
import org.juba.espressoapp.data.remote.RemoteChange
import org.juba.espressoapp.data.remote.RemoteChangeType
import org.juba.espressoapp.data.remote.RemoteDocument
import org.juba.espressoapp.data.remote.SyncCollection
import org.juba.espressoapp.data.remote.toCoffeeBeanEntity
import org.juba.espressoapp.data.remote.toEspressoMachineEntity
import org.juba.espressoapp.data.remote.toFilterBasketEntity
import org.juba.espressoapp.data.remote.toGrinderEntity
import org.juba.espressoapp.data.remote.toRoasterEntity
import org.juba.espressoapp.data.remote.toShotLogEntity
import org.juba.espressoapp.logging.AppLog
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "EspressoSync"

/** Parent lookups are transient during the initial load, so only report a stall occasionally. */
private const val WAITING_LOG_INTERVAL_MS = 2_000L

/**
 * Applies remote changes to Room.
 *
 * Changes are buffered and applied in [SyncCollection] dependency order, because Room enforces
 * the `ON DELETE RESTRICT` foreign keys and a shot can arrive before the equipment it points
 * to. A document whose parents have not arrived yet stays pending and is retried when the next
 * change is applied.
 *
 * Conflicts use last write wins on `updated_at`: a remote document is applied only when it is
 * strictly newer than the local row. Ties are skipped, the local copy is already that version.
 * An edit made directly in the console therefore also has to bump `updatedAt`.
 */
@Singleton
class SyncApplier @Inject constructor(
    private val roasterDao: RoasterDao,
    private val coffeeBeanDao: CoffeeBeanDao,
    private val grinderDao: GrinderDao,
    private val espressoMachineDao: EspressoMachineDao,
    private val filterBasketDao: FilterBasketDao,
    private val shotLogDao: ShotLogDao,
) {
    private val mutex = Mutex()
    private val pending = linkedMapOf<String, RemoteChange>()
    private var lastWaitingLogAt = 0L

    /** Applies [change], serialized so the six listeners cannot interleave their writes. */
    suspend fun apply(change: RemoteChange) = mutex.withLock {
        if (change.type == RemoteChangeType.Removed) {
            // The app never hard-deletes remotely, so a removal can only come from the
            // console. Ignoring it keeps local data intact.
            AppLog.w(TAG) { "ignoring remote removal of ${change.document.key}" }
            return@withLock
        }
        pending[change.document.key] = change
        drain()
    }

    /** Drops buffered changes, used when the local data is wiped on an identity change. */
    suspend fun clearPending() = mutex.withLock { pending.clear() }

    private suspend fun drain() {
        while (true) {
            val snapshot = pending.values.toList()
            if (snapshot.isEmpty()) return

            var appliedAny = false
            for (collection in SyncCollection.entries) {
                snapshot
                    .filter { it.document.collection == collection }
                    .forEach { change ->
                        when (applyOne(change)) {
                            ApplyResult.MissingParent -> Unit
                            else -> {
                                pending.remove(change.document.key)
                                appliedAny = true
                            }
                        }
                    }
            }

            if (!appliedAny) {
                val now = SystemClock.elapsedRealtime()
                if (now - lastWaitingLogAt >= WAITING_LOG_INTERVAL_MS) {
                    lastWaitingLogAt = now
                    AppLog.d(TAG) {
                        "waiting for parents of ${pending.size} documents: ${pending.keys.joinToString()}"
                    }
                }
                return
            }
            lastWaitingLogAt = 0L
        }
    }

    private suspend fun applyOne(change: RemoteChange): ApplyResult {
        return try {
            when (change.document.collection) {
                SyncCollection.ROASTERS -> {
                    val entity = change.document.toRoasterEntity() ?: return invalid(change)
                    applyIfNewer(change, entity.updatedAt, roasterDao.getByIdIncludingDeleted(entity.id)?.updatedAt) {
                        roasterDao.upsert(entity)
                    }
                }

                SyncCollection.COFFEE_BEANS -> {
                    val entity = change.document.toCoffeeBeanEntity() ?: return invalid(change)
                    applyIfNewer(change, entity.updatedAt, coffeeBeanDao.getByIdIncludingDeleted(entity.id)?.updatedAt) {
                        coffeeBeanDao.upsert(entity)
                    }
                }

                SyncCollection.GRINDERS -> {
                    val entity = change.document.toGrinderEntity() ?: return invalid(change)
                    applyIfNewer(change, entity.updatedAt, grinderDao.getByIdIncludingDeleted(entity.id)?.updatedAt) {
                        grinderDao.upsert(entity)
                    }
                }

                SyncCollection.ESPRESSO_MACHINES -> {
                    val entity = change.document.toEspressoMachineEntity() ?: return invalid(change)
                    applyIfNewer(
                        change,
                        entity.updatedAt,
                        espressoMachineDao.getByIdIncludingDeleted(entity.id)?.updatedAt,
                    ) {
                        espressoMachineDao.upsert(entity)
                    }
                }

                SyncCollection.FILTER_BASKETS -> {
                    val entity = change.document.toFilterBasketEntity() ?: return invalid(change)
                    applyIfNewer(
                        change,
                        entity.updatedAt,
                        filterBasketDao.getByIdIncludingDeleted(entity.id)?.updatedAt,
                    ) {
                        filterBasketDao.upsert(entity)
                    }
                }

                SyncCollection.SHOT_LOGS -> {
                    val entity = change.document.toShotLogEntity() ?: return invalid(change)
                    applyIfNewer(change, entity.updatedAt, shotLogDao.getByIdIncludingDeleted(entity.id)?.updatedAt) {
                        shotLogDao.upsert(entity)
                    }
                }
            }
        } catch (error: SQLiteConstraintException) {
            ApplyResult.MissingParent
        }
    }

    private suspend fun applyIfNewer(
        change: RemoteChange,
        remoteUpdatedAt: Long,
        localUpdatedAt: Long?,
        upsert: suspend () -> Unit,
    ): ApplyResult {
        if (localUpdatedAt != null && localUpdatedAt >= remoteUpdatedAt) {
            // Equal timestamps are the echo of our own push, which is the common case on every
            // start, so only a strictly newer local copy is worth a line.
            if (localUpdatedAt > remoteUpdatedAt) {
                AppLog.d(TAG) {
                    "skip ${change.document.key}, local ${localUpdatedAt}ms is newer than remote ${remoteUpdatedAt}ms"
                }
            }
            return ApplyResult.Skipped
        }
        upsert()
        return ApplyResult.Applied
    }

    private fun invalid(change: RemoteChange): ApplyResult {
        AppLog.w(TAG) { "skipping invalid remote ${change.document.key}" }
        return ApplyResult.Invalid
    }
}

private enum class ApplyResult { Applied, Skipped, Invalid, MissingParent }

private val RemoteDocument.key: String get() = "${collection.path}/$id"
