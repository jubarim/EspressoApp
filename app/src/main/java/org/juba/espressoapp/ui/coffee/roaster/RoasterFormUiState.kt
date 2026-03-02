package org.juba.espressoapp.ui.coffee.roaster

data class RoasterFormUiState(
    val name: String = "",
    val country: String = "",
    val website: String = "",
    val logoUrl: String = "",
    val notes: String = "",
    val nameError: String? = null,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
)
