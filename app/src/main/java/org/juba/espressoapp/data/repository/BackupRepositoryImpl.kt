package org.juba.espressoapp.data.repository

import android.content.Context
import android.net.Uri
import androidx.room.withTransaction
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import org.juba.espressoapp.data.backup.BackupDto
import org.juba.espressoapp.data.backup.toDto
import org.juba.espressoapp.data.backup.toEntity
import org.juba.espressoapp.data.local.dao.CoffeeBeanDao
import org.juba.espressoapp.data.local.dao.EspressoMachineDao
import org.juba.espressoapp.data.local.dao.FilterBasketDao
import org.juba.espressoapp.data.local.dao.GrinderDao
import org.juba.espressoapp.data.local.dao.RoasterDao
import org.juba.espressoapp.data.local.dao.ShotLogDao
import org.juba.espressoapp.data.local.database.AppDatabase
import org.juba.espressoapp.domain.repository.BackupRepository
import javax.inject.Inject

class BackupRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val db: AppDatabase,
    private val roasterDao: RoasterDao,
    private val coffeeBeanDao: CoffeeBeanDao,
    private val grinderDao: GrinderDao,
    private val espressoMachineDao: EspressoMachineDao,
    private val filterBasketDao: FilterBasketDao,
    private val shotLogDao: ShotLogDao,
) : BackupRepository {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        prettyPrint = false
    }

    override suspend fun exportBackup(uri: Uri): Result<Unit> = runCatching {
        val backup = BackupDto(
            exportedAt = System.currentTimeMillis(),
            roasters = roasterDao.getAll().first().map { it.toDto() },
            coffeeBeans = coffeeBeanDao.getAllEntities().first().map { it.toDto() },
            grinders = grinderDao.getAll().first().map { it.toDto() },
            espressoMachines = espressoMachineDao.getAll().first().map { it.toDto() },
            filterBaskets = filterBasketDao.getAll().first().map { it.toDto() },
            shotLogs = shotLogDao.getAllEntities().first().map { it.toDto() },
        )
        val jsonString = json.encodeToString(BackupDto.serializer(), backup)
        context.contentResolver.openOutputStream(uri)?.use { stream ->
            stream.write(jsonString.toByteArray(Charsets.UTF_8))
        } ?: error("Could not open output stream for URI: $uri")
    }

    override suspend fun importBackup(uri: Uri): Result<Unit> = runCatching {
        val jsonString = context.contentResolver.openInputStream(uri)?.use { stream ->
            stream.readBytes().toString(Charsets.UTF_8)
        } ?: error("Could not open input stream for URI: $uri")

        val backup = json.decodeFromString<BackupDto>(jsonString)

        db.withTransaction {
            // Delete in reverse FK order
            shotLogDao.deleteAll()
            filterBasketDao.deleteAll()
            espressoMachineDao.deleteAll()
            grinderDao.deleteAll()
            coffeeBeanDao.deleteAll()
            roasterDao.deleteAll()

            // Insert in FK order
            roasterDao.insertAll(backup.roasters.map { it.toEntity() })
            coffeeBeanDao.insertAll(backup.coffeeBeans.map { it.toEntity() })
            grinderDao.insertAll(backup.grinders.map { it.toEntity() })
            espressoMachineDao.insertAll(backup.espressoMachines.map { it.toEntity() })
            filterBasketDao.insertAll(backup.filterBaskets.map { it.toEntity() })
            shotLogDao.insertAll(backup.shotLogs.map { it.toEntity() })
        }
    }
}
