package com.codecini.chefia.ui.features.generate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codecini.chefia.domain.model.Recipe
import com.codecini.chefia.domain.model.RecipeGenerationMode
import com.codecini.chefia.domain.usecase.GenerateRecipeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GenerateRecipeUiState(
    val inputText: String = "",
    val ingredients: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedMode: RecipeGenerationMode = RecipeGenerationMode.STRICT
)

@HiltViewModel
class GenerateRecipeViewModel @Inject constructor(
    private val generateRecipeUseCase: GenerateRecipeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GenerateRecipeUiState())
    val uiState = _uiState.asStateFlow()

    private val _generatedRecipe = MutableStateFlow<Recipe?>(null)
    val generatedRecipe = _generatedRecipe.asStateFlow()

    fun onInputTextChange(text: String) {
        _uiState.update { it.copy(inputText = text) }
        if (text.endsWith(",")) {
            addIngredient(text.dropLast(1))
        }
    }

    fun addIngredient(ingredient: String) {
        val trimmed = ingredient.trim()
        if (trimmed.isNotBlank() && !_uiState.value.ingredients.contains(trimmed)) {
            _uiState.update {
                it.copy(
                    ingredients = it.ingredients + trimmed,
                    inputText = ""
                )
            }
        }
    }

    fun removeIngredient(ingredient: String) {
        _uiState.update {
            it.copy(ingredients = it.ingredients - ingredient)
        }
    }

    fun onGenerationModeSelected(mode: RecipeGenerationMode) {
        _uiState.update { it.copy(selectedMode = mode) }
    }

    fun generateRecipe() {
        if (_uiState.value.ingredients.isEmpty()) {
            _uiState.update { it.copy(error = "Adicione pelo menos um ingrediente.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = generateRecipeUseCase(
                ingredients = _uiState.value.ingredients,
                mode = _uiState.value.selectedMode
            )
            result.onSuccess { recipe ->
                _generatedRecipe.value = recipe
            }.onFailure {
                _uiState.update { it.copy(error = "Não foi possível gerar a receita. Tente novamente.") }
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onNavigationHandled() {
        _generatedRecipe.value = null
    }
}

