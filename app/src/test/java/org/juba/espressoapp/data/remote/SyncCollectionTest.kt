package org.juba.espressoapp.data.remote

import org.junit.Assert.assertEquals
import org.junit.Test

class SyncCollectionTest {

    @Test
    fun `collections are declared parents before children`() {
        assertEquals(
            listOf(
                SyncCollection.ROASTERS,
                SyncCollection.COFFEE_BEANS,
                SyncCollection.GRINDERS,
                SyncCollection.ESPRESSO_MACHINES,
                SyncCollection.FILTER_BASKETS,
                SyncCollection.SHOT_LOGS,
            ),
            SyncCollection.entries.toList(),
        )
    }
}
