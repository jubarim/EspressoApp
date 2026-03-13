package org.juba.espressoapp.ui.gear.filterbasket

data class FilterBasketFormUiState(
    val brand: String = "",
    val model: String = "",
    val sizeGrams: String = "",
    val type: String = "",
    val diameter: String = "",
    val purchaseDate: Long? = null,
    val imageUrl: String = "",
    val notes: String = "",
    val brandError: String? = null,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
)
