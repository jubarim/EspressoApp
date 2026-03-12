package org.juba.espressoapp.ui.coffee.coffeebean

import org.juba.espressoapp.domain.model.Roaster

data class CoffeeBeanFormUiState(
    val name: String = "",
    val roasterId: String = "",
    val roasterName: String = "",
    val origin: String = "",
    val process: String = "",
    val roastLevel: String = "",
    val roastDate: Long? = null,
    val imageUrl: String = "",
    val notes: String = "",
    val nameError: String? = null,
    val roasterError: String? = null,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val roasters: List<Roaster> = emptyList(),
)
