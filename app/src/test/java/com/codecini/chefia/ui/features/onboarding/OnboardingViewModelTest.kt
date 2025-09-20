package com.codecini.chefia.ui.features.onboarding

import com.codecini.chefia.domain.repository.UserPreferencesRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {

    private lateinit var userPreferencesRepository: UserPreferencesRepository

    private lateinit var viewModel: OnboardingViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        userPreferencesRepository = mockk(relaxed = true)
        viewModel = OnboardingViewModel(userPreferencesRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onOnboardingComplete should call repository to save completion state`() = runTest {
        viewModel.onOnboardingComplete()

        testDispatcher.scheduler.runCurrent()

        coVerify(exactly = 1) { userPreferencesRepository.saveOnboardingComplete() }
    }
}

