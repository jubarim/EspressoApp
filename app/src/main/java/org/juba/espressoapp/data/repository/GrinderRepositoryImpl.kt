package org.juba.espressoapp.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.juba.espressoapp.data.local.dao.GrinderDao
import org.juba.espressoapp.data.local.mapper.toDomain
import org.juba.espressoapp.data.local.mapper.toEntity
import org.juba.espressoapp.domain.model.Grinder
import org.juba.espressoapp.domain.repository.GrinderRepository
import javax.inject.Inject

class GrinderRepositoryImpl @Inject constructor(
    private val dao: GrinderDao,
) : GrinderRepository {

    override fun getAll(): Flow<List<Grinder>> =
        dao.getAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getById(id: String): Grinder? =
        dao.getById(id)?.toDomain()

    override suspend fun insert(grinder: Grinder): Result<Unit> = runCatching {
        dao.insert(grinder.toEntity())
    }

    override suspend fun update(grinder: Grinder): Result<Unit> = runCatching {
        dao.update(grinder.toEntity())
    }

    override suspend fun delete(id: String): Result<Unit> = runCatching {
        dao.softDelete(id, System.currentTimeMillis())
    }
}
