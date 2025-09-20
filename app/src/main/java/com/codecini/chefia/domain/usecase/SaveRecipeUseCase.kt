package com.codecini.chefia.domain.usecase

import com.codecini.chefia.domain.model.Recipe
import com.codecini.chefia.domain.repository.RecipeRepository
import javax.inject.Inject

class SaveRecipeUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(recipe: Recipe) = repository.saveRecipe(recipe)
}
