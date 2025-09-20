package com.codecini.chefia.ui.features.home

import app.cash.turbine.test
import com.codecini.chefia.domain.model.Recipe
import com.codecini.chefia.domain.usecase.DeleteRecipeUseCase
import com.codecini.chefia.domain.usecase.GetSavedRecipesUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private lateinit var getSavedRecipesUseCase: GetSavedRecipesUseCase
    private lateinit var deleteRecipeUseCase: DeleteRecipeUseCase

    private lateinit var viewModel: HomeViewModel

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var recipesFlow: MutableStateFlow<List<Recipe>>

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        recipesFlow = MutableStateFlow(emptyList())

        getSavedRecipesUseCase = mockk()
        every { getSavedRecipesUseCase() } returns recipesFlow

        deleteRecipeUseCase = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading, then stable with empty recipes`() = runTest {
        viewModel = HomeViewModel(getSavedRecipesUseCase, deleteRecipeUseCase)

        viewModel.uiState.test {
            val loadingState = awaitItem()
            assertThat(loadingState.isLoading).isTrue()

            val initialState = awaitItem()
            assertThat(initialState.recipes).isEmpty()
            assertThat(initialState.isLoading).isFalse()
            assertThat(initialState.isSelectionMode).isFalse()
            assertThat(initialState.selectedRecipesIds).isEmpty()
        }
    }

    @Test
    fun `when recipes are emitted, uiState should be updated`() = runTest {
        val mockRecipes = listOf(Recipe(1L, "Bolo", "Farinha", "Asse"))
        viewModel = HomeViewModel(getSavedRecipesUseCase, deleteRecipeUseCase)

        viewModel.uiState.test {
            awaitItem()
            awaitItem()

            recipesFlow.value = mockRecipes

            val updatedState = awaitItem()
            assertThat(updatedState.recipes).isEqualTo(mockRecipes)
        }
    }

    @Test
    fun `onRecipeLongClick should enable selection mode and select the recipe`() = runTest {
        viewModel = HomeViewModel(getSavedRecipesUseCase, deleteRecipeUseCase)
        val recipe = Recipe(1L, "Torta", "Massa", "Asse")

        viewModel.uiState.test {
            awaitItem()
            awaitItem()

            viewModel.onRecipeLongClick(recipe)

            val selectionState = awaitItem()
            assertThat(selectionState.isSelectionMode).isTrue()
            assertThat(selectionState.selectedRecipesIds).containsExactly(1L)
        }
    }

    @Test
    fun `onRecipeClick in selection mode should toggle selection`() = runTest {
        viewModel = HomeViewModel(getSavedRecipesUseCase, deleteRecipeUseCase)
        val recipe1 = Recipe(1L, "A", "a", "a")
        val recipe2 = Recipe(2L, "B", "b", "b")

        viewModel.uiState.test {
            awaitItem()
            awaitItem()

            viewModel.onRecipeLongClick(recipe1)

            viewModel.onRecipeClick(recipe2)
            val stateWithTwoSelected = awaitItem()
            assertThat(stateWithTwoSelected.selectedRecipesIds).containsExactly(1L, 2L)

            viewModel.onRecipeClick(recipe1)
            val stateWithOneSelected = awaitItem()
            assertThat(stateWithOneSelected.selectedRecipesIds).containsExactly(2L)
        }
    }

    @Test
    fun `deselecting the last item should exit selection mode`() = runTest {
        viewModel = HomeViewModel(getSavedRecipesUseCase, deleteRecipeUseCase)
        val recipe = Recipe(1L, "A", "a", "a")

        viewModel.uiState.test {
            awaitItem()
            awaitItem()

            viewModel.onRecipeLongClick(recipe)
            awaitItem()

            viewModel.onRecipeClick(recipe)

            val finalState = awaitItem()
            assertThat(finalState.isSelectionMode).isFalse()
            assertThat(finalState.selectedRecipesIds).isEmpty()
        }
    }

    @Test
    fun `deleteSelectedRecipes should call use case for each id and clear selection`() = runTest {
        viewModel = HomeViewModel(getSavedRecipesUseCase, deleteRecipeUseCase)
        val recipe1 = Recipe(1L, "A", "a", "a")
        val recipe2 = Recipe(2L, "B", "b", "b")
        viewModel.onRecipeLongClick(recipe1)
        testDispatcher.scheduler.runCurrent()
        viewModel.onRecipeClick(recipe2)
        testDispatcher.scheduler.runCurrent()

        viewModel.deleteSelectedRecipes()
        testDispatcher.scheduler.runCurrent()

        coVerify(exactly = 1) { deleteRecipeUseCase(1L) }
        coVerify(exactly = 1) { deleteRecipeUseCase(2L) }

        assertThat(viewModel.uiState.value.isSelectionMode).isFalse()
        assertThat(viewModel.uiState.value.selectedRecipesIds).isEmpty()
    }
}

