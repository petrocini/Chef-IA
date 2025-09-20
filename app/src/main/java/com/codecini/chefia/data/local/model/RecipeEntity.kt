package com.codecini.chefia.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.codecini.chefia.domain.model.Recipe

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val ingredients: String,
    val instructions: String
)

fun RecipeEntity.toDomainModel(): Recipe {
    return Recipe(
        id = this.id,
        title = this.title,
        ingredients = this.ingredients,
        instructions = this.instructions
    )
}

fun Recipe.toEntity(): RecipeEntity {
    return RecipeEntity(
        id = if (this.id == 0L) 0 else this.id,
        title = this.title,
        ingredients = this.ingredients,
        instructions = this.instructions
    )
}
