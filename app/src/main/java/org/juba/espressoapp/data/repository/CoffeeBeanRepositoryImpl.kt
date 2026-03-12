package org.juba.espressoapp.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.juba.espressoapp.data.local.dao.CoffeeBeanDao
import org.juba.espressoapp.data.local.mapper.toDomain
import org.juba.espressoapp.data.local.mapper.toEntity
import org.juba.espressoapp.domain.model.CoffeeBean
import org.juba.espressoapp.domain.repository.CoffeeBeanRepository
import javax.inject.Inject

class CoffeeBeanRepositoryImpl @Inject constructor(
    private val dao: CoffeeBeanDao,
) : CoffeeBeanRepository {

    override fun getAll(): Flow<List<CoffeeBean>> =
        dao.getAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getById(id: String): CoffeeBean? =
        dao.getById(id)?.toDomain()

    override suspend fun insert(bean: CoffeeBean): Result<Unit> = runCatching {
        dao.insert(bean.toEntity())
    }

    override suspend fun update(bean: CoffeeBean): Result<Unit> = runCatching {
        dao.update(bean.toEntity())
    }

    override suspend fun delete(id: String): Result<Unit> = runCatching {
        dao.softDelete(id, System.currentTimeMillis())
    }
}
