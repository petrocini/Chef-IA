package com.codecini.chefia.domain.repository

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val shouldShowOnboarding: Flow<Boolean>
    suspend fun saveOnboardingComplete()
}
