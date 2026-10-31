package org.juba.espressoapp.ui.filter

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ShotFilterStoreTest {

    private val store = ShotFilterStore()

    @Test
    fun `starts with no filter`() {
        assertNull(store.filter.value)
    }

    @Test
    fun `set applies a filter`() {
        val filter = ShotFilter.CoffeeBean(coffeeBeanId = "bean-1", label = "Ethiopia")

        store.set(filter)

        assertEquals(filter, store.filter.value)
    }

    @Test
    fun `a new filter replaces the previous one`() {
        store.set(ShotFilter.CoffeeBean(coffeeBeanId = "bean-1", label = "Ethiopia"))
        val roasterFilter = ShotFilter.Roaster(roasterId = "roaster-1", label = "Square Mile")

        store.set(roasterFilter)

        assertEquals(roasterFilter, store.filter.value)
    }

    @Test
    fun `clear removes the filter`() {
        store.set(ShotFilter.Roaster(roasterId = "roaster-1", label = "Square Mile"))

        store.clear()

        assertNull(store.filter.value)
    }
}
