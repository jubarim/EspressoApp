package org.juba.espressoapp.ui.filter

import org.juba.espressoapp.domain.model.CoffeeBean
import org.juba.espressoapp.domain.model.ShotLog

/**
 * Returns the shots that match [filter], preserving the original order.
 *
 * A coffee bean filter matches the exact bean record. A roaster filter matches every shot whose
 * bean belongs to that roaster. A null filter returns the list unchanged.
 *
 * @param filter Filter to apply, or null to keep every shot.
 * @param coffeeBeans Beans used to resolve the roaster of each shot.
 */
fun List<ShotLog>.filteredBy(
    filter: ShotFilter?,
    coffeeBeans: List<CoffeeBean>,
): List<ShotLog> = when (filter) {
    null -> this
    is ShotFilter.CoffeeBean -> this.filter { it.coffeeBeanId == filter.coffeeBeanId }
    is ShotFilter.Roaster -> {
        val beanIds = coffeeBeans
            .asSequence()
            .filter { it.roasterId == filter.roasterId }
            .map { it.id }
            .toSet()
        this.filter { it.coffeeBeanId in beanIds }
    }
}
