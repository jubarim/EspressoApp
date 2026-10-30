package org.juba.espressoapp.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.juba.espressoapp.data.local.dao.FilterBasketDao
import org.juba.espressoapp.data.local.mapper.toDomain
import org.juba.espressoapp.data.local.mapper.toEntity
import org.juba.espressoapp.data.remote.toRemoteDocument
import org.juba.espressoapp.data.sync.RemoteWriteDispatcher
import org.juba.espressoapp.domain.model.FilterBasket
import org.juba.espressoapp.domain.repository.FilterBasketRepository
import javax.inject.Inject

class FilterBasketRepositoryImpl @Inject constructor(
    private val dao: FilterBasketDao,
    private val remoteWriteDispatcher: RemoteWriteDispatcher,
) : FilterBasketRepository {

    override fun getAll(): Flow<List<FilterBasket>> =
        dao.getAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getById(id: String): FilterBasket? =
        dao.getById(id)?.toDomain()

    override suspend fun insert(basket: FilterBasket): Result<Unit> = runCatching {
        val entity = basket.toEntity()
        dao.insert(entity)
        remoteWriteDispatcher.dispatch(entity.toRemoteDocument())
    }

    override suspend fun update(basket: FilterBasket): Result<Unit> = runCatching {
        val entity = basket.toEntity()
        dao.update(entity)
        remoteWriteDispatcher.dispatch(entity.toRemoteDocument())
    }

    override suspend fun delete(id: String): Result<Unit> = runCatching {
        dao.softDelete(id, System.currentTimeMillis())
        dao.getByIdIncludingDeleted(id)?.let { remoteWriteDispatcher.dispatch(it.toRemoteDocument()) }
    }
}
