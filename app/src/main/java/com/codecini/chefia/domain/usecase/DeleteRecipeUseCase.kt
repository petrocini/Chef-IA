package com.codecini.chefia.domain.usecase

import com.codecini.chefia.domain.model.Recipe
import com.codecini.chefia.domain.repository.RecipeRepository
import javax.inject.Inject

class DeleteRecipeUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(id: Long) {
        val recipeToDelete = Recipe(id = id, title = "", ingredients = "", instructions = "")
        repository.deleteRecipe(recipeToDelete)
    }
}

