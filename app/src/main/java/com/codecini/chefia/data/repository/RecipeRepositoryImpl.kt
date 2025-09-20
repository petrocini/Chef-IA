package com.codecini.chefia.data.repository

import com.codecini.chefia.data.local.dao.RecipeDao
import com.codecini.chefia.data.local.model.toDomainModel
import com.codecini.chefia.data.local.model.toEntity
import com.codecini.chefia.data.remote.GenerativeAiDataSource
import com.codecini.chefia.domain.model.Recipe
import com.codecini.chefia.domain.model.RecipeGenerationMode
import com.codecini.chefia.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipeRepositoryImpl @Inject constructor(
    private val recipeDao: RecipeDao,
    private val remoteDataSource: GenerativeAiDataSource
) : RecipeRepository {

    override suspend fun generateRecipe(ingredients: String, mode: RecipeGenerationMode): Result<Recipe> {
        return remoteDataSource.generateRecipe(ingredients, mode)
    }

    override suspend fun saveRecipe(recipe: Recipe) {
        recipeDao.insertRecipe(recipe.toEntity())
    }

    override suspend fun deleteRecipe(recipe: Recipe) {
        recipeDao.deleteRecipe(recipe.toEntity())
    }

    override fun getSavedRecipes(): Flow<List<Recipe>> {
        return recipeDao.getAllRecipes().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    override fun getRecipeById(id: Long): Flow<Recipe?> {
        return recipeDao.getRecipeById(id).map { entity ->
            entity?.toDomainModel()
        }
    }
}
