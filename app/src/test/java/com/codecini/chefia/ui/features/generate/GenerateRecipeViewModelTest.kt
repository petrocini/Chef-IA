package com.codecini.chefia.ui.features.generate

import app.cash.turbine.test
import com.codecini.chefia.domain.usecase.GenerateRecipeUseCase
import com.google.common.truth.Truth.assertThat
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
class GenerateRecipeViewModelTest {

    private lateinit var generateRecipeUseCase: GenerateRecipeUseCase

    private lateinit var viewModel: GenerateRecipeViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        generateRecipeUseCase = mockk(relaxed = true)
        viewModel = GenerateRecipeViewModel(generateRecipeUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `addIngredient should add valid ingredient and clear input`() = runTest {
        viewModel.uiState.test {
            var state = awaitItem()
            assertThat(state.ingredients).isEmpty()
            assertThat(state.inputText).isEmpty()

            viewModel.addIngredient("Arroz")

            state = awaitItem()
            assertThat(state.ingredients).containsExactly("Arroz")
            assertThat(state.inputText).isEmpty()

            viewModel.addIngredient("Feijão")

            state = awaitItem()
            assertThat(state.ingredients).containsExactly("Arroz", "Feijão")
            assertThat(state.inputText).isEmpty()
        }
    }

    @Test
    fun `addIngredient should not add blank ingredients`() = runTest {
        viewModel.uiState.test {
            val initialState = awaitItem()

            viewModel.addIngredient("   ")

            ensureAllEventsConsumed()
            assertThat(viewModel.uiState.value.ingredients).isEqualTo(initialState.ingredients)
        }
    }

    @Test
    fun `addIngredient should not add duplicate ingredients`() = runTest {
        viewModel.uiState.test {
            awaitItem()

            viewModel.addIngredient("Frango")
            viewModel.addIngredient(" Frango ")

            val finalState = awaitItem()
            assertThat(finalState.ingredients).containsExactly("Frango")
        }
    }

    @Test
    fun `removeIngredient should remove the correct ingredient`() = runTest {
        viewModel.uiState.test {
            assertThat(awaitItem().ingredients).isEmpty()

            viewModel.addIngredient("Tomate")
            awaitItem()
            viewModel.addIngredient("Cebola")

            val stateBeforeRemove = awaitItem()
            assertThat(stateBeforeRemove.ingredients).containsExactly("Tomate", "Cebola")

            viewModel.removeIngredient("Tomate")

            val stateAfterRemove = awaitItem()
            assertThat(stateAfterRemove.ingredients).containsExactly("Cebola")
        }
    }
}

