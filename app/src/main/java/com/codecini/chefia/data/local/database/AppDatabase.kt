package com.codecini.chefia.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.codecini.chefia.data.local.dao.RecipeDao
import com.codecini.chefia.data.local.model.RecipeEntity

@Database(
    entities = [RecipeEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao
}
