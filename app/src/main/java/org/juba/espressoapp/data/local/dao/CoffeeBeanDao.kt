package org.juba.espressoapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.juba.espressoapp.data.local.entity.CoffeeBeanEntity
import org.juba.espressoapp.data.local.entity.CoffeeBeanWithRoaster

@Dao
interface CoffeeBeanDao {

    @Query(
        """
        SELECT cb.*, r.name AS roaster_name
        FROM coffee_beans cb
        LEFT JOIN roasters r ON cb.roaster_id = r.id
        WHERE cb.is_deleted = 0
        ORDER BY cb.name ASC
        """,
    )
    fun getAll(): Flow<List<CoffeeBeanWithRoaster>>

    @Query(
        """
        SELECT cb.*, r.name AS roaster_name
        FROM coffee_beans cb
        LEFT JOIN roasters r ON cb.roaster_id = r.id
        WHERE cb.id = :id AND cb.is_deleted = 0
        LIMIT 1
        """,
    )
    suspend fun getById(id: String): CoffeeBeanWithRoaster?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: CoffeeBeanEntity)

    @Update
    suspend fun update(entity: CoffeeBeanEntity)

    @Query("UPDATE coffee_beans SET is_deleted = 1, updated_at = :timestamp WHERE id = :id")
    suspend fun softDelete(id: String, timestamp: Long)
}
