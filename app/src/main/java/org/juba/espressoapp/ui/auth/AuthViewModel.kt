package org.juba.espressoapp.ui.auth

import android.app.Activity
import android.util.Log
import androidx.credentials.CreatePublicKeyCredentialRequest
import androidx.credentials.CreatePublicKeyCredentialResponse
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPublicKeyCredentialOption
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.CreateCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.juba.espressoapp.domain.repository.AuthRepository
import javax.inject.Inject

private const val TAG = "jm_PasskeyAuth"

sealed class AuthEvent {
    data object NavigateToMain : AuthEvent()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val credentialManager: CredentialManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AuthEvent>()
    val events: SharedFlow<AuthEvent> = _events.asSharedFlow()

    /** Registers a new passkey for [displayName], using [activity] to anchor the system UI. */
    fun register(displayName: String, activity: Activity) {
        if (!validate(displayName)) return
        Log.d(TAG, "register: starting passkey registration for '$displayName'")
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.prepareRegistration(displayName)
                .mapCatching { challengeJson ->
                    Log.d(TAG, "register: challenge ready, showing biometric prompt")
                    val request = CreatePublicKeyCredentialRequest(requestJson = challengeJson)
                    val result = credentialManager.createCredential(activity, request)
                    Log.d(TAG, "register: biometric prompt completed, sending credential to server")
                    (result as CreatePublicKeyCredentialResponse).registrationResponseJson
                }
                .mapCatching { responseJson ->
                    authRepository.completeRegistration(responseJson).getOrThrow()
                }
                .onSuccess {
                    Log.d(TAG, "register: registration successful")
                    onAuthSuccess()
                }
                .onFailure {
                    Log.e(TAG, "register: FAILED", it)
                    _uiState.value = AuthUiState.Error(it.toUserMessage())
                }
        }
    }

    /** Signs in using an existing passkey for [displayName], using [activity] to anchor the system UI. */
    fun signIn(displayName: String, activity: Activity) {
        if (!validate(displayName)) return
        Log.d(TAG, "signIn: starting passkey sign-in for '$displayName'")
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.getLoginChallenge(displayName)
                .mapCatching { challengeJson ->
                    Log.d(TAG, "signIn: challenge ready, showing biometric prompt")
                    val option = GetPublicKeyCredentialOption(requestJson = challengeJson)
                    val request = GetCredentialRequest(listOf(option))
                    val result = credentialManager.getCredential(activity, request)
                    Log.d(TAG, "signIn: biometric prompt completed, sending assertion to server")
                    (result.credential as PublicKeyCredential).authenticationResponseJson
                }
                .mapCatching { authResponseJson ->
                    authRepository.completeLogin(authResponseJson).getOrThrow()
                }
                .onSuccess {
                    Log.d(TAG, "signIn: sign-in successful")
                    onAuthSuccess()
                }
                .onFailure {
                    Log.e(TAG, "signIn: FAILED", it)
                    _uiState.value = AuthUiState.Error(it.toUserMessage())
                }
        }
    }

    private fun validate(displayName: String): Boolean {
        if (displayName.isBlank()) {
            _uiState.value = AuthUiState.Error("Please enter your name")
            return false
        }
        return true
    }

    private suspend fun onAuthSuccess() {
        _uiState.value = AuthUiState.Idle
        _events.emit(AuthEvent.NavigateToMain)
    }

    private fun Throwable.toUserMessage(): String = when (this) {
        is GetCredentialCancellationException -> "Sign-in cancelled"
        is CreateCredentialCancellationException -> "Passkey creation cancelled"
        else -> message ?: "An unexpected error occurred"
    }
}
