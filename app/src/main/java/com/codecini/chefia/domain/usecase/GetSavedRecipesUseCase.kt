package com.codecini.chefia.domain.usecase

import com.codecini.chefia.domain.repository.RecipeRepository
import javax.inject.Inject

class GetSavedRecipesUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    operator fun invoke() = repository.getSavedRecipes()
}
