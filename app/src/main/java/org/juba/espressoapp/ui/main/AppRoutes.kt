package org.juba.espressoapp.ui.main

internal object AppRoutes {
    const val SHOTS = "shots"
    const val COFFEE_HOME = "coffee_home"
    const val ROASTER_LIST = "roaster_list"
    const val ROASTER_DETAIL = "roaster_detail/{roasterId}"
    const val ROASTER_FORM = "roaster_form?roasterId={roasterId}"
    const val GEAR = "gear"
    const val SETTINGS = "settings"
}

/**
 * Only routes in this set will show the bottom navigation bar.
 */
internal val showBottomNavBar = setOf(
    AppRoutes.SHOTS,
    AppRoutes.COFFEE_HOME,
    AppRoutes.ROASTER_LIST,
    AppRoutes.GEAR,
    AppRoutes.SETTINGS,
)
