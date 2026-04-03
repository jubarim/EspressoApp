package org.juba.espressoapp.domain.repository

import kotlinx.coroutines.flow.Flow
import org.juba.espressoapp.domain.model.ShotLog

interface ShotLogRepository {
    fun getAll(): Flow<List<ShotLog>>
    suspend fun getById(id: String): ShotLog?
    suspend fun insert(shot: ShotLog): Result<Unit>
    suspend fun update(shot: ShotLog): Result<Unit>
    suspend fun delete(id: String): Result<Unit>
}
