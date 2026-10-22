package org.juba.espressoapp.ui.auth

import androidx.annotation.StringRes

sealed class AuthUiState {
    data object Idle : AuthUiState()
    data object Loading : AuthUiState()
    data class Error(@param:StringRes val messageRes: Int) : AuthUiState()
}
