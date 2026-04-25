package org.juba.espressoapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.juba.espressoapp.data.local.entity.RoasterEntity

@Dao
interface RoasterDao {

    @Query("SELECT * FROM roasters WHERE is_deleted = 0 ORDER BY name ASC")
    fun getAll(): Flow<List<RoasterEntity>>

    @Query("SELECT * FROM roasters WHERE id = :id AND is_deleted = 0 LIMIT 1")
    suspend fun getById(id: String): RoasterEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: RoasterEntity)

    @Update
    suspend fun update(entity: RoasterEntity)

    @Query("UPDATE roasters SET is_deleted = 1, updated_at = :timestamp WHERE id = :id")
    suspend fun softDelete(id: String, timestamp: Long)

    @Query("DELETE FROM roasters")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<RoasterEntity>)
}
