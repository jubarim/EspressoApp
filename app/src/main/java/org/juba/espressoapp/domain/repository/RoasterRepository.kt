package org.juba.espressoapp.domain.repository

import kotlinx.coroutines.flow.Flow
import org.juba.espressoapp.domain.model.Roaster

interface RoasterRepository {

    /** Emits the list of active (non-deleted) roasters, updated reactively. */
    fun getAll(): Flow<List<Roaster>>

    /** Returns a single roaster by [id], or null if not found or deleted. */
    suspend fun getById(id: String): Roaster?

    /** Inserts a new roaster. Returns [Result.failure] on error. */
    suspend fun insert(roaster: Roaster): Result<Unit>

    /** Updates an existing roaster. Returns [Result.failure] on error. */
    suspend fun update(roaster: Roaster): Result<Unit>

    /** Soft-deletes the roaster with the given [id]. Returns [Result.failure] on error. */
    suspend fun delete(id: String): Result<Unit>
}
