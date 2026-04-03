package org.juba.espressoapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.juba.espressoapp.data.local.entity.ShotLogEntity
import org.juba.espressoapp.data.local.entity.ShotLogWithDetails

@Dao
interface ShotLogDao {

    @Query(
        """
        SELECT sl.*,
               cb.name AS coffee_bean_name,
               r.name AS roaster_name,
               g.brand || ' ' || g.model AS grinder_name,
               em.brand || ' ' || em.model AS machine_name,
               fb.brand || COALESCE(' ' || fb.model, '') AS basket_name
        FROM shot_logs sl
        LEFT JOIN coffee_beans cb ON sl.coffee_bean_id = cb.id
        LEFT JOIN roasters r ON cb.roaster_id = r.id
        LEFT JOIN grinders g ON sl.grinder_id = g.id
        LEFT JOIN espresso_machines em ON sl.machine_id = em.id
        LEFT JOIN filter_baskets fb ON sl.basket_id = fb.id
        WHERE sl.is_deleted = 0
        ORDER BY sl.shot_at DESC
        """,
    )
    fun getAll(): Flow<List<ShotLogWithDetails>>

    @Query(
        """
        SELECT sl.*,
               cb.name AS coffee_bean_name,
               r.name AS roaster_name,
               g.brand || ' ' || g.model AS grinder_name,
               em.brand || ' ' || em.model AS machine_name,
               fb.brand || COALESCE(' ' || fb.model, '') AS basket_name
        FROM shot_logs sl
        LEFT JOIN coffee_beans cb ON sl.coffee_bean_id = cb.id
        LEFT JOIN roasters r ON cb.roaster_id = r.id
        LEFT JOIN grinders g ON sl.grinder_id = g.id
        LEFT JOIN espresso_machines em ON sl.machine_id = em.id
        LEFT JOIN filter_baskets fb ON sl.basket_id = fb.id
        WHERE sl.id = :id AND sl.is_deleted = 0
        LIMIT 1
        """,
    )
    suspend fun getById(id: String): ShotLogWithDetails?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: ShotLogEntity)

    @Update
    suspend fun update(entity: ShotLogEntity)

    @Query("UPDATE shot_logs SET is_deleted = 1, updated_at = :timestamp WHERE id = :id")
    suspend fun softDelete(id: String, timestamp: Long)
}
