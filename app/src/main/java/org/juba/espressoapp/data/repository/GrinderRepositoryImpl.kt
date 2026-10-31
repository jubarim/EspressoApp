package org.juba.espressoapp.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.juba.espressoapp.data.local.dao.GrinderDao
import org.juba.espressoapp.data.local.mapper.toDomain
import org.juba.espressoapp.data.local.mapper.toEntity
import org.juba.espressoapp.data.remote.toRemoteDocument
import org.juba.espressoapp.data.sync.RemoteWriteDispatcher
import org.juba.espressoapp.domain.model.Grinder
import org.juba.espressoapp.domain.repository.GrinderRepository
import javax.inject.Inject

class GrinderRepositoryImpl @Inject constructor(
    private val dao: GrinderDao,
    private val remoteWriteDispatcher: RemoteWriteDispatcher,
) : GrinderRepository {

    override fun getAll(): Flow<List<Grinder>> =
        dao.getAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getById(id: String): Grinder? =
        dao.getById(id)?.toDomain()

    override suspend fun insert(grinder: Grinder): Result<Unit> = runCatching {
        val entity = grinder.toEntity()
        dao.insert(entity)
        remoteWriteDispatcher.dispatch(entity.toRemoteDocument())
    }

    override suspend fun update(grinder: Grinder): Result<Unit> = runCatching {
        val entity = grinder.toEntity()
        dao.update(entity)
        remoteWriteDispatcher.dispatch(entity.toRemoteDocument())
    }

    override suspend fun delete(id: String): Result<Unit> = runCatching {
        dao.softDelete(id, System.currentTimeMillis())
        dao.getByIdIncludingDeleted(id)?.let { remoteWriteDispatcher.dispatch(it.toRemoteDocument()) }
    }
}
