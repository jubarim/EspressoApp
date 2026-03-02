package org.juba.espressoapp.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.juba.espressoapp.data.local.dao.RoasterDao
import org.juba.espressoapp.data.local.mapper.toDomain
import org.juba.espressoapp.data.local.mapper.toEntity
import org.juba.espressoapp.domain.model.Roaster
import org.juba.espressoapp.domain.repository.RoasterRepository
import javax.inject.Inject

class RoasterRepositoryImpl @Inject constructor(
    private val dao: RoasterDao,
) : RoasterRepository {

    override fun getAll(): Flow<List<Roaster>> =
        dao.getAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getById(id: String): Roaster? =
        dao.getById(id)?.toDomain()

    override suspend fun insert(roaster: Roaster): Result<Unit> = runCatching {
        dao.insert(roaster.toEntity())
    }

    override suspend fun update(roaster: Roaster): Result<Unit> = runCatching {
        dao.update(roaster.toEntity())
    }

    override suspend fun delete(id: String): Result<Unit> = runCatching {
        dao.softDelete(id, System.currentTimeMillis())
    }
}
