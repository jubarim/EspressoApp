package org.juba.espressoapp.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.juba.espressoapp.data.local.dao.ShotLogDao
import org.juba.espressoapp.data.local.mapper.toDomain
import org.juba.espressoapp.data.local.mapper.toEntity
import org.juba.espressoapp.domain.model.ShotLog
import org.juba.espressoapp.domain.repository.ShotLogRepository
import javax.inject.Inject

class ShotLogRepositoryImpl @Inject constructor(
    private val dao: ShotLogDao,
) : ShotLogRepository {

    override fun getAll(): Flow<List<ShotLog>> =
        dao.getAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getById(id: String): ShotLog? =
        dao.getById(id)?.toDomain()

    override suspend fun insert(shot: ShotLog): Result<Unit> = runCatching {
        dao.insert(shot.toEntity())
    }

    override suspend fun update(shot: ShotLog): Result<Unit> = runCatching {
        dao.update(shot.toEntity())
    }

    override suspend fun delete(id: String): Result<Unit> = runCatching {
        dao.softDelete(id, System.currentTimeMillis())
    }
}
