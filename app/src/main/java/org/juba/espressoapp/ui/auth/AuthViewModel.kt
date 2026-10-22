package org.juba.espressoapp.ui.auth

import android.app.Activity
import androidx.annotation.StringRes
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.juba.espressoapp.R
import org.juba.espressoapp.domain.repository.AuthRepository
import org.juba.espressoapp.domain.repository.PreferencesRepository
import javax.inject.Inject

sealed class AuthEvent {
    data object NavigateToMain : AuthEvent()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val preferencesRepository: PreferencesRepository,
    private val credentialManager: CredentialManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AuthEvent>()
    val events: SharedFlow<AuthEvent> = _events.asSharedFlow()

    /**
     * Starts the Google sign-in flow, using [activity] to anchor the system UI.
     *
     * @param serverClientId OAuth web client ID the Google ID token is issued for.
     */
    fun signInWithGoogle(activity: Activity, serverClientId: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            runCatching { requestGoogleIdToken(activity, serverClientId) }
                .mapCatching { idToken -> authRepository.signInWithGoogle(idToken).getOrThrow() }
                .onSuccess {
                    preferencesRepository.setOnboardingComplete()
                    onAuthSuccess()
                }
                .onFailure { _uiState.value = AuthUiState.Error(it.toUserMessageRes()) }
        }
    }

    /** Skips sign-in and enters the app in guest mode, without creating an account. */
    fun continueAsGuest() {
        viewModelScope.launch {
            preferencesRepository.setOnboardingComplete()
            onAuthSuccess()
        }
    }

    private suspend fun requestGoogleIdToken(activity: Activity, serverClientId: String): String {
        val option = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(serverClientId)
            .setAutoSelectEnabled(false)
            .build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(option)
            .build()

        val credential = credentialManager.getCredential(activity, request).credential
        check(credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            "Unexpected credential type: ${credential.type}"
        }
        return GoogleIdTokenCredential.createFrom(credential.data).idToken
    }

    private suspend fun onAuthSuccess() {
        _uiState.value = AuthUiState.Idle
        _events.emit(AuthEvent.NavigateToMain)
    }

    @StringRes
    private fun Throwable.toUserMessageRes(): Int = when (this) {
        is GetCredentialCancellationException -> R.string.auth_error_cancelled
        is NoCredentialException -> R.string.auth_error_no_account
        else -> R.string.auth_error_generic
    }
}
