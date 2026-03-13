package org.juba.espressoapp.ui.gear.grinder

data class GrinderFormUiState(
    val brand: String = "",
    val model: String = "",
    val burrType: String = "",
    val burrSize: String = "",
    val purchaseDate: Long? = null,
    val burrInstallDate: Long? = null,
    val imageUrl: String = "",
    val notes: String = "",
    val brandError: String? = null,
    val modelError: String? = null,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
)
