package org.juba.espressoapp.domain.repository

import android.net.Uri

interface BackupRepository {
    /** Serializes all non-deleted records to JSON and writes to the given SAF [uri]. */
    suspend fun exportBackup(uri: Uri): Result<Unit>

    /**
     * Reads the JSON file at [uri], clears all local data in reverse FK order,
     * then inserts the backup records in FK order — all within a single transaction.
     */
    suspend fun importBackup(uri: Uri): Result<Unit>
}
