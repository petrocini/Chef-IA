package com.codecini.chefia.ui.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codecini.chefia.domain.model.Recipe
import com.codecini.chefia.domain.usecase.DeleteRecipeUseCase
import com.codecini.chefia.domain.usecase.GetSavedRecipesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val recipes: List<Recipe> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val isSelectionMode: Boolean = false,
    val selectedRecipesIds: Set<Long> = emptySet()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    getSavedRecipesUseCase: GetSavedRecipesUseCase,
    private val deleteRecipeUseCase: DeleteRecipeUseCase
) : ViewModel() {

    private val _isSelectionMode = MutableStateFlow(false)
    private val _selectedRecipesIds = MutableStateFlow<Set<Long>>(emptySet())
    private val _recipes = getSavedRecipesUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val uiState: StateFlow<HomeUiState> = combine(
        _recipes, _isSelectionMode, _selectedRecipesIds
    ) { recipes, isSelectionMode, selectedIds ->
        HomeUiState(
            recipes = recipes,
            isLoading = false,
            isSelectionMode = isSelectionMode,
            selectedRecipesIds = selectedIds
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    fun onRecipeClick(recipe: Recipe) {
        if (_isSelectionMode.value) {
            toggleRecipeSelection(recipe.id)
        }
    }

    fun onRecipeLongClick(recipe: Recipe) {
        _isSelectionMode.value = true
        toggleRecipeSelection(recipe.id)
    }

    private fun toggleRecipeSelection(id: Long) {
        val currentSelection = _selectedRecipesIds.value.toMutableSet()
        if (currentSelection.contains(id)) {
            currentSelection.remove(id)
        } else {
            currentSelection.add(id)
        }
        _selectedRecipesIds.value = currentSelection

        if (currentSelection.isEmpty()) {
            clearSelection()
        }
    }

    fun clearSelection() {
        _isSelectionMode.value = false
        _selectedRecipesIds.value = emptySet()
    }

    fun deleteSelectedRecipes() {
        viewModelScope.launch {
            _selectedRecipesIds.value.forEach { id ->
                deleteRecipeUseCase(id)
            }
            clearSelection()
        }
    }
}

