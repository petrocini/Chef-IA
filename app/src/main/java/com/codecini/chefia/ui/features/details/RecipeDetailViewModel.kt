package com.codecini.chefia.ui.features.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codecini.chefia.domain.model.Recipe
import com.codecini.chefia.domain.usecase.DeleteRecipeUseCase
import com.codecini.chefia.domain.usecase.GetRecipeByIdUseCase
import com.codecini.chefia.domain.usecase.SaveRecipeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject

data class RecipeDetailUiState(
    val recipe: Recipe? = null,
    val isLoading: Boolean = true,
    val isSaved: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class RecipeDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val saveRecipeUseCase: SaveRecipeUseCase,
    private val deleteRecipeUseCase: DeleteRecipeUseCase,
    private val getRecipeByIdUseCase: GetRecipeByIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeDetailUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val recipeId = savedStateHandle.get<Long>("recipeId")
        if (recipeId != null && recipeId != -1L) {
            viewModelScope.launch {
                getRecipeByIdUseCase(recipeId).collectLatest { recipe ->
                    if (recipe != null) {
                        _uiState.update {
                            it.copy(recipe = recipe, isLoading = false, isSaved = true)
                        }
                    } else {
                        _uiState.update {
                            it.copy(isLoading = false, error = "Receita não encontrada.")
                        }
                    }
                }
            }
        } else {
            val encodedTitle = savedStateHandle.get<String>("title") ?: ""
            val encodedIngredients = savedStateHandle.get<String>("ingredients") ?: ""
            val encodedInstructions = savedStateHandle.get<String>("instructions") ?: ""

            val title = URLDecoder.decode(encodedTitle, StandardCharsets.UTF_8.toString())
            val ingredients = URLDecoder.decode(encodedIngredients, StandardCharsets.UTF_8.toString())
            val instructions = URLDecoder.decode(encodedInstructions, StandardCharsets.UTF_8.toString())

            val recipe = Recipe(title = title, ingredients = ingredients, instructions = instructions)
            _uiState.update { it.copy(recipe = recipe, isLoading = false, isSaved = false) }
        }
    }

    fun saveRecipe() {
        _uiState.value.recipe?.let { recipe ->
            viewModelScope.launch {
                saveRecipeUseCase(recipe)
                _uiState.update { it.copy(isSaved = true) }
            }
        }
    }

    fun deleteRecipe() {
        _uiState.value.recipe?.id?.let { id ->
            viewModelScope.launch {
                deleteRecipeUseCase(id)
            }
        }
    }
}

