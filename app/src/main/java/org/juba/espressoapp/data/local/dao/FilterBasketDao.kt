package org.juba.espressoapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.juba.espressoapp.data.local.entity.FilterBasketEntity

@Dao
interface FilterBasketDao {

    @Query("SELECT * FROM filter_baskets WHERE is_deleted = 0 ORDER BY brand ASC")
    fun getAll(): Flow<List<FilterBasketEntity>>

    @Query("SELECT * FROM filter_baskets WHERE id = :id AND is_deleted = 0 LIMIT 1")
    suspend fun getById(id: String): FilterBasketEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: FilterBasketEntity)

    @Update
    suspend fun update(entity: FilterBasketEntity)

    @Query("UPDATE filter_baskets SET is_deleted = 1, updated_at = :timestamp WHERE id = :id")
    suspend fun softDelete(id: String, timestamp: Long)
}
