package org.juba.espressoapp.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.juba.espressoapp.data.local.dao.EspressoMachineDao
import org.juba.espressoapp.data.local.mapper.toDomain
import org.juba.espressoapp.data.local.mapper.toEntity
import org.juba.espressoapp.domain.model.EspressoMachine
import org.juba.espressoapp.domain.repository.EspressoMachineRepository
import javax.inject.Inject

class EspressoMachineRepositoryImpl @Inject constructor(
    private val dao: EspressoMachineDao,
) : EspressoMachineRepository {

    override fun getAll(): Flow<List<EspressoMachine>> =
        dao.getAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getById(id: String): EspressoMachine? =
        dao.getById(id)?.toDomain()

    override suspend fun insert(machine: EspressoMachine): Result<Unit> = runCatching {
        dao.insert(machine.toEntity())
    }

    override suspend fun update(machine: EspressoMachine): Result<Unit> = runCatching {
        dao.update(machine.toEntity())
    }

    override suspend fun delete(id: String): Result<Unit> = runCatching {
        dao.softDelete(id, System.currentTimeMillis())
    }
}
