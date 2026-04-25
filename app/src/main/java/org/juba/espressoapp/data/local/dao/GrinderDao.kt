package org.juba.espressoapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.juba.espressoapp.data.local.entity.GrinderEntity

@Dao
interface GrinderDao {

    @Query("SELECT * FROM grinders WHERE is_deleted = 0 ORDER BY brand ASC, model ASC")
    fun getAll(): Flow<List<GrinderEntity>>

    @Query("SELECT * FROM grinders WHERE id = :id AND is_deleted = 0 LIMIT 1")
    suspend fun getById(id: String): GrinderEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: GrinderEntity)

    @Update
    suspend fun update(entity: GrinderEntity)

    @Query("UPDATE grinders SET is_deleted = 1, updated_at = :timestamp WHERE id = :id")
    suspend fun softDelete(id: String, timestamp: Long)

    @Query("DELETE FROM grinders")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<GrinderEntity>)
}
