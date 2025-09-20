package com.codecini.chefia.ui.features.details

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.codecini.chefia.domain.model.Recipe
import com.codecini.chefia.domain.usecase.DeleteRecipeUseCase
import com.codecini.chefia.domain.usecase.GetRecipeByIdUseCase
import com.codecini.chefia.domain.usecase.SaveRecipeUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeDetailViewModelTest {

    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var saveRecipeUseCase: SaveRecipeUseCase
    private lateinit var deleteRecipeUseCase: DeleteRecipeUseCase
    private lateinit var getRecipeByIdUseCase: GetRecipeByIdUseCase

    private lateinit var viewModel: RecipeDetailViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        savedStateHandle = mockk(relaxed = true)
        saveRecipeUseCase = mockk(relaxed = true)
        deleteRecipeUseCase = mockk(relaxed = true)
        getRecipeByIdUseCase = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init with new recipe arguments should decode them and set state correctly`() = runTest {
        val title = "Bolo de Cenoura"
        val ingredients = "- Cenoura\n- Farinha"
        val instructions = "Misture tudo."

        val encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8.toString())
        val encodedIngredients = URLEncoder.encode(ingredients, StandardCharsets.UTF_8.toString())
        val encodedInstructions = URLEncoder.encode(instructions, StandardCharsets.UTF_8.toString())

        every { savedStateHandle.get<Long>("recipeId") } returns -1L
        every { savedStateHandle.get<String>("title") } returns encodedTitle
        every { savedStateHandle.get<String>("ingredients") } returns encodedIngredients
        every { savedStateHandle.get<String>("instructions") } returns encodedInstructions

        viewModel = RecipeDetailViewModel(savedStateHandle, saveRecipeUseCase, deleteRecipeUseCase, getRecipeByIdUseCase)

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.isLoading).isFalse()
            assertThat(state.isSaved).isFalse()
            assertThat(state.recipe).isNotNull()
            assertThat(state.recipe?.title).isEqualTo(title)
            assertThat(state.recipe?.ingredients).isEqualTo(ingredients)
            assertThat(state.recipe?.instructions).isEqualTo(instructions)
        }
    }

    @Test
    fun `init with existing recipe id should fetch recipe and set state correctly`() = runTest {
        val recipeId = 123L
        val mockRecipe = Recipe(id = recipeId, title = "Torta de Frango", ingredients = "Frango", instructions = "Asse.")
        every { savedStateHandle.get<Long>("recipeId") } returns recipeId
        every { getRecipeByIdUseCase(recipeId) } returns flowOf(mockRecipe)

        viewModel = RecipeDetailViewModel(savedStateHandle, saveRecipeUseCase, deleteRecipeUseCase, getRecipeByIdUseCase)

        viewModel.uiState.test {
            val loadingState = awaitItem()
            assertThat(loadingState.isLoading).isTrue()

            val finalState = awaitItem()
            assertThat(finalState.isLoading).isFalse()
            assertThat(finalState.isSaved).isTrue()
            assertThat(finalState.recipe).isEqualTo(mockRecipe)
        }
    }

    @Test
    fun `saveRecipe should call use case and update state`() = runTest {
        val newRecipe = Recipe(title = "Pão Caseiro", ingredients = "Farinha", instructions = "Soque.")
        every { savedStateHandle.get<Long>("recipeId") } returns -1L
        every { savedStateHandle.get<String>("title") } returns newRecipe.title
        every { savedStateHandle.get<String>("ingredients") } returns newRecipe.ingredients
        every { savedStateHandle.get<String>("instructions") } returns newRecipe.instructions

        viewModel = RecipeDetailViewModel(savedStateHandle, saveRecipeUseCase, deleteRecipeUseCase, getRecipeByIdUseCase)

        viewModel.uiState.test {
            val initialState = awaitItem()
            assertThat(initialState.isSaved).isFalse()

            viewModel.saveRecipe()
            testDispatcher.scheduler.runCurrent()

            coVerify { saveRecipeUseCase(newRecipe) }

            val finalState = awaitItem()
            assertThat(finalState.isSaved).isTrue()
        }
    }

    @Test
    fun `deleteRecipe should call use case`() = runTest {
        val recipeId = 456L
        val mockRecipe = Recipe(id = recipeId, title = "Salada", ingredients = "Alface", instructions = "Tempere.")
        every { savedStateHandle.get<Long>("recipeId") } returns recipeId
        every { getRecipeByIdUseCase(recipeId) } returns flowOf(mockRecipe)

        viewModel = RecipeDetailViewModel(savedStateHandle, saveRecipeUseCase, deleteRecipeUseCase, getRecipeByIdUseCase)

        testDispatcher.scheduler.runCurrent()

        viewModel.deleteRecipe()
        testDispatcher.scheduler.runCurrent()

        coVerify(exactly = 1) { deleteRecipeUseCase(recipeId) }
    }
}

