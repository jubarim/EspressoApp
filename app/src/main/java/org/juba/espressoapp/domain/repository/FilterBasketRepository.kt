package org.juba.espressoapp.domain.repository

import kotlinx.coroutines.flow.Flow
import org.juba.espressoapp.domain.model.FilterBasket

interface FilterBasketRepository {
    fun getAll(): Flow<List<FilterBasket>>
    suspend fun getById(id: String): FilterBasket?
    suspend fun insert(basket: FilterBasket): Result<Unit>
    suspend fun update(basket: FilterBasket): Result<Unit>
    suspend fun delete(id: String): Result<Unit>
}
