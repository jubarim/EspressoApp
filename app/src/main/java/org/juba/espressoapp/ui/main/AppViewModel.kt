package org.juba.espressoapp.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import org.juba.espressoapp.domain.repository.AuthRepository
import org.juba.espressoapp.domain.repository.PreferencesRepository
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    authRepository: AuthRepository,
    preferencesRepository: PreferencesRepository,
) : ViewModel() {

    /**
     * Whether the welcome screen should be shown.
     * Emits null until the initial state is known, then true when there is no signed-in user
     * and the first-launch choice has not been made (first launch, or after signing out).
     */
    val showWelcome: StateFlow<Boolean?> = combine(
        preferencesRepository.isOnboardingComplete(),
        authRepository.currentUser(),
    ) { onboardingComplete, user -> !onboardingComplete && user == null }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)
}
