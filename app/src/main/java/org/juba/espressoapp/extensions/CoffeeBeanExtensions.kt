package org.juba.espressoapp.extensions

import org.juba.espressoapp.domain.model.CoffeeBean
import java.util.UUID

/**
 * Returns a copy of this coffee bean with a new identity and roast date.
 *
 * @param roastDate Roast date to use for the copy.
 * @param now Timestamp used for the copy's creation and update times.
 * @return A new [CoffeeBean] that keeps every other field from the original.
 */
fun CoffeeBean.duplicate(
    roastDate: Long?,
    now: Long = System.currentTimeMillis(),
): CoffeeBean = copy(
    id = UUID.randomUUID().toString(),
    roastDate = roastDate,
    createdAt = now,
    updatedAt = now,
)
