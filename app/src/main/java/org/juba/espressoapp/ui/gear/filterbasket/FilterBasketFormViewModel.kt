package org.juba.espressoapp.ui.gear.filterbasket

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.juba.espressoapp.domain.model.FilterBasket
import org.juba.espressoapp.domain.repository.FilterBasketRepository
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class FilterBasketFormViewModel @Inject constructor(
    private val repository: FilterBasketRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val basketId: String? = savedStateHandle[BASKET_ID]

    private val _uiState = MutableStateFlow(FilterBasketFormUiState())
    val uiState: StateFlow<FilterBasketFormUiState> = _uiState.asStateFlow()

    init {
        basketId?.let { loadBasket(it) }
    }

    private fun loadBasket(id: String) {
        viewModelScope.launch {
            repository.getById(id)?.let { basket ->
                _uiState.update {
                    it.copy(
                        brand = basket.brand,
                        model = basket.model ?: "",
                        sizeGrams = basket.sizeGrams ?: "",
                        type = basket.type ?: "",
                        diameter = basket.diameter ?: "",
                        purchaseDate = basket.purchaseDate,
                        imageUrl = basket.imageUri ?: "",
                        notes = basket.notes ?: "",
                    )
                }
            }
        }
    }

    fun onBrandChange(value: String) = _uiState.update { it.copy(brand = value, brandError = null) }

    fun onModelChange(value: String) = _uiState.update { it.copy(model = value) }

    fun onSizeGramsChange(value: String) = _uiState.update { it.copy(sizeGrams = value) }

    fun onTypeChange(value: String) = _uiState.update { it.copy(type = value) }

    fun onDiameterChange(value: String) = _uiState.update { it.copy(diameter = value) }

    fun onPurchaseDateChange(value: Long?) = _uiState.update { it.copy(purchaseDate = value) }

    fun onImageUrlChange(value: String) = _uiState.update { it.copy(imageUrl = value) }

    fun onNotesChange(value: String) = _uiState.update { it.copy(notes = value) }

    fun save() {
        val state = _uiState.value
        if (state.brand.isBlank()) {
            _uiState.update { it.copy(brandError = "Brand is required") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val now = System.currentTimeMillis()
            val basket = FilterBasket(
                id = basketId ?: UUID.randomUUID().toString(),
                brand = state.brand.trim(),
                model = state.model.trim().ifBlank { null },
                sizeGrams = state.sizeGrams.trim().ifBlank { null },
                type = state.type.ifBlank { null },
                diameter = state.diameter.ifBlank { null },
                purchaseDate = state.purchaseDate,
                imageUri = state.imageUrl.trim().ifBlank { null },
                notes = state.notes.trim().ifBlank { null },
                createdAt = now,
                updatedAt = now,
            )
            val result = if (basketId == null) repository.insert(basket)
            else repository.update(basket)

            _uiState.update {
                if (result.isSuccess) it.copy(isSaving = false, isSaved = true)
                else it.copy(isSaving = false)
            }
        }
    }

    companion object {
        const val BASKET_ID = "basketId"
    }
}
