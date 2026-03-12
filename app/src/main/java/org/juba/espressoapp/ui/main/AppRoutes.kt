package org.juba.espressoapp.ui.main

internal object AppRoutes {
    const val SHOTS = "shots"
    const val COFFEE_HOME = "coffee_home"
    const val ROASTER_LIST = "roaster_list"
    const val ROASTER_DETAIL = "roaster_detail/{roasterId}"
    const val ROASTER_FORM = "roaster_form?roasterId={roasterId}"
    const val COFFEE_BEAN_LIST = "coffee_bean_list"
    const val COFFEE_BEAN_DETAIL = "coffee_bean_detail/{beanId}"
    const val COFFEE_BEAN_FORM = "coffee_bean_form?beanId={beanId}"
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
    AppRoutes.COFFEE_BEAN_LIST,
    AppRoutes.GEAR,
    AppRoutes.SETTINGS,
)
