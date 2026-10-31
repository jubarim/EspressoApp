package org.juba.espressoapp.extensions

import org.juba.espressoapp.domain.model.ShotLog
import java.util.UUID

/**
 * Returns a copy of this shot with a new identity and shot date.
 *
 * @param shotAt Timestamp to use as the new shot's date.
 * @param now Timestamp used for the copy's creation and update times.
 * @return A new [ShotLog] that keeps every other field from the original.
 */
fun ShotLog.duplicate(
    shotAt: Long,
    now: Long = System.currentTimeMillis(),
): ShotLog = copy(
    id = UUID.randomUUID().toString(),
    shotAt = shotAt,
    createdAt = now,
    updatedAt = now,
)
