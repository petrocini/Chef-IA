package com.codecini.chefia.domain.repository

import com.codecini.chefia.domain.model.Recipe
import com.codecini.chefia.domain.model.RecipeGenerationMode
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
    suspend fun generateRecipe(ingredients: String, mode: RecipeGenerationMode): Result<Recipe>
    suspend fun saveRecipe(recipe: Recipe)
    suspend fun deleteRecipe(recipe: Recipe)
    fun getSavedRecipes(): Flow<List<Recipe>>
    fun getRecipeById(id: Long): Flow<Recipe?>
}

