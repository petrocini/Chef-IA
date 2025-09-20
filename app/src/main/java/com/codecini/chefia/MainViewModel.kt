package com.codecini.chefia

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codecini.chefia.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed class StartDestination {
    data object Onboarding : StartDestination()
    data object Home : StartDestination()
    data object Loading : StartDestination()
}

@HiltViewModel
class MainViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val startDestination: StateFlow<StartDestination> =
        userPreferencesRepository.shouldShowOnboarding
            .map { shouldShow ->
                if (shouldShow) StartDestination.Onboarding else StartDestination.Home
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = StartDestination.Loading
            )
}
