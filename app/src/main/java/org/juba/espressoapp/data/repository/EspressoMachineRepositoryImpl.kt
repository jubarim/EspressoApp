package org.juba.espressoapp.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.juba.espressoapp.data.local.dao.EspressoMachineDao
import org.juba.espressoapp.data.local.mapper.toDomain
import org.juba.espressoapp.data.local.mapper.toEntity
import org.juba.espressoapp.data.remote.toRemoteDocument
import org.juba.espressoapp.data.sync.RemoteWriteDispatcher
import org.juba.espressoapp.domain.model.EspressoMachine
import org.juba.espressoapp.domain.repository.EspressoMachineRepository
import javax.inject.Inject

class EspressoMachineRepositoryImpl @Inject constructor(
    private val dao: EspressoMachineDao,
    private val remoteWriteDispatcher: RemoteWriteDispatcher,
) : EspressoMachineRepository {

    override fun getAll(): Flow<List<EspressoMachine>> =
        dao.getAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getById(id: String): EspressoMachine? =
        dao.getById(id)?.toDomain()

    override suspend fun insert(machine: EspressoMachine): Result<Unit> = runCatching {
        val entity = machine.toEntity()
        dao.insert(entity)
        remoteWriteDispatcher.dispatch(entity.toRemoteDocument())
    }

    override suspend fun update(machine: EspressoMachine): Result<Unit> = runCatching {
        val entity = machine.toEntity()
        dao.update(entity)
        remoteWriteDispatcher.dispatch(entity.toRemoteDocument())
    }

    override suspend fun delete(id: String): Result<Unit> = runCatching {
        dao.softDelete(id, System.currentTimeMillis())
        dao.getByIdIncludingDeleted(id)?.let { remoteWriteDispatcher.dispatch(it.toRemoteDocument()) }
    }
}
