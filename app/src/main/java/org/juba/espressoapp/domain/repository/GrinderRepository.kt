package org.juba.espressoapp.domain.repository

import kotlinx.coroutines.flow.Flow
import org.juba.espressoapp.domain.model.Grinder

interface GrinderRepository {
    fun getAll(): Flow<List<Grinder>>
    suspend fun getById(id: String): Grinder?
    suspend fun insert(grinder: Grinder): Result<Unit>
    suspend fun update(grinder: Grinder): Result<Unit>
    suspend fun delete(id: String): Result<Unit>
}
