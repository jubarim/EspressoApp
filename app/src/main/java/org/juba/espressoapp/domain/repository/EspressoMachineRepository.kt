package org.juba.espressoapp.domain.repository

import kotlinx.coroutines.flow.Flow
import org.juba.espressoapp.domain.model.EspressoMachine

interface EspressoMachineRepository {
    fun getAll(): Flow<List<EspressoMachine>>
    suspend fun getById(id: String): EspressoMachine?
    suspend fun insert(machine: EspressoMachine): Result<Unit>
    suspend fun update(machine: EspressoMachine): Result<Unit>
    suspend fun delete(id: String): Result<Unit>
}
