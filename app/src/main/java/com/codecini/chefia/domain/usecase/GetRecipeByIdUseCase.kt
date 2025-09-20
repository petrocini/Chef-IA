package com.codecini.chefia.domain.usecase

import com.codecini.chefia.domain.model.Recipe
import com.codecini.chefia.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecipeByIdUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    operator fun invoke(id: Long): Flow<Recipe?> = repository.getRecipeById(id)
}
