package com.codecini.chefia.domain.usecase

import com.codecini.chefia.domain.model.Recipe
import com.codecini.chefia.domain.model.RecipeGenerationMode
import com.codecini.chefia.domain.repository.RecipeRepository
import javax.inject.Inject

class GenerateRecipeUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(ingredients: List<String>, mode: RecipeGenerationMode): Result<Recipe> {
        val ingredientsAsString = ingredients.joinToString(separator = ", ")
        return repository.generateRecipe(ingredientsAsString, mode)
    }
}

