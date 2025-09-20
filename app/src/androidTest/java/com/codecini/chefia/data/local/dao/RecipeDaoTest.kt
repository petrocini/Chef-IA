package com.codecini.chefia.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.codecini.chefia.data.local.database.AppDatabase
import com.codecini.chefia.data.local.model.RecipeEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecipeDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: RecipeDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).build()
        dao = db.recipeDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertRecipe_and_getById_returnsCorrectRecipe() {
        runBlocking {
            val recipe = RecipeEntity(id = 1, title = "Bolo de Chocolate", ingredients = "Chocolate", instructions = "Asse")

            dao.insertRecipe(recipe)

            val loaded = dao.getRecipeById(recipe.id).first()
            assertThat(loaded).isNotNull()
            assertThat(loaded?.id).isEqualTo(recipe.id)
            assertThat(loaded?.title).isEqualTo(recipe.title)
        }
    }

    @Test
    fun getAllRecipes_returnsAllInsertedRecipes() {
        runBlocking {
            val recipe1 = RecipeEntity(id = 1, title = "Receita A", ingredients = "A", instructions = "a")
            val recipe2 = RecipeEntity(id = 2, title = "Receita B", ingredients = "B", instructions = "b")
            dao.insertRecipe(recipe1)
            dao.insertRecipe(recipe2)

            val allRecipes = dao.getAllRecipes().first()

            assertThat(allRecipes).hasSize(2)
            assertThat(allRecipes).containsExactly(recipe2, recipe1)
        }
    }

    @Test
    fun deleteRecipe_removesRecipeFromDatabase() {
        runBlocking {
            val recipe = RecipeEntity(id = 1, title = "Receita para deletar", ingredients = "...", instructions = "...")
            dao.insertRecipe(recipe)

            assertThat(dao.getRecipeById(recipe.id).first()).isNotNull()

            dao.deleteRecipe(recipe)

            assertThat(dao.getRecipeById(recipe.id).first()).isNull()
        }
    }
}

