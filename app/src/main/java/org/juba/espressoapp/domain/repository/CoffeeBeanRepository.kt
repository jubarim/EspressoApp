package org.juba.espressoapp.domain.repository

import kotlinx.coroutines.flow.Flow
import org.juba.espressoapp.domain.model.CoffeeBean

interface CoffeeBeanRepository {
    fun getAll(): Flow<List<CoffeeBean>>
    suspend fun getById(id: String): CoffeeBean?
    suspend fun insert(bean: CoffeeBean): Result<Unit>
    suspend fun update(bean: CoffeeBean): Result<Unit>
    suspend fun delete(id: String): Result<Unit>
}
