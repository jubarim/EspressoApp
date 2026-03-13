package org.juba.espressoapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.juba.espressoapp.data.local.entity.EspressoMachineEntity

@Dao
interface EspressoMachineDao {

    @Query("SELECT * FROM espresso_machines WHERE is_deleted = 0 ORDER BY brand ASC, model ASC")
    fun getAll(): Flow<List<EspressoMachineEntity>>

    @Query("SELECT * FROM espresso_machines WHERE id = :id AND is_deleted = 0 LIMIT 1")
    suspend fun getById(id: String): EspressoMachineEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: EspressoMachineEntity)

    @Update
    suspend fun update(entity: EspressoMachineEntity)

    @Query("UPDATE espresso_machines SET is_deleted = 1, updated_at = :timestamp WHERE id = :id")
    suspend fun softDelete(id: String, timestamp: Long)
}
