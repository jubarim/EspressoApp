package org.juba.espressoapp.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.juba.espressoapp.data.local.dao.CoffeeBeanDao
import org.juba.espressoapp.data.local.mapper.toDomain
import org.juba.espressoapp.data.local.mapper.toEntity
import org.juba.espressoapp.data.remote.toRemoteDocument
import org.juba.espressoapp.data.sync.RemoteWriteDispatcher
import org.juba.espressoapp.domain.model.CoffeeBean
import org.juba.espressoapp.domain.repository.CoffeeBeanRepository
import javax.inject.Inject

class CoffeeBeanRepositoryImpl @Inject constructor(
    private val dao: CoffeeBeanDao,
    private val remoteWriteDispatcher: RemoteWriteDispatcher,
) : CoffeeBeanRepository {

    override fun getAll(): Flow<List<CoffeeBean>> =
        dao.getAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getById(id: String): CoffeeBean? =
        dao.getById(id)?.toDomain()

    override suspend fun insert(bean: CoffeeBean): Result<Unit> = runCatching {
        val entity = bean.toEntity()
        dao.insert(entity)
        remoteWriteDispatcher.dispatch(entity.toRemoteDocument())
    }

    override suspend fun update(bean: CoffeeBean): Result<Unit> = runCatching {
        val entity = bean.toEntity()
        dao.update(entity)
        remoteWriteDispatcher.dispatch(entity.toRemoteDocument())
    }

    override suspend fun delete(id: String): Result<Unit> = runCatching {
        dao.softDelete(id, System.currentTimeMillis())
        dao.getByIdIncludingDeleted(id)?.let { remoteWriteDispatcher.dispatch(it.toRemoteDocument()) }
    }
}
