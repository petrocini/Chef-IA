package com.codecini.chefia.ui.util

import android.content.Context
import android.content.Intent
import com.codecini.chefia.domain.model.Recipe

fun shareRecipe(context: Context, recipe: Recipe) {
    val shareText = """
        Olha que receita incrível que eu gerei com o Chef IA!
        
        *${recipe.title.trim()}*
        
        *Ingredientes:*
        ${recipe.ingredients.trim()}
        
        *Modo de Preparo:*
        ${recipe.instructions.trim()}
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Receita: ${recipe.title}")
        putExtra(Intent.EXTRA_TEXT, shareText)
    }

    context.startActivity(
        Intent.createChooser(
            intent,
            "Compartilhar receita via"
        )
    )
}

fun shareMultipleRecipes(context: Context, recipes: List<Recipe>) {
    val shareText = buildString {
        append("Olha que receitas incríveis que eu gerei com o Chef IA!\n\n")
        append("--- LISTA DE RECEITAS ---\n\n")

        recipes.forEachIndexed { index, recipe ->
            append("*${index + 1}. ${recipe.title.trim()}*\n\n")
            append("*Ingredientes:*\n${recipe.ingredients.trim()}\n\n")
            append("*Modo de Preparo:*\n${recipe.instructions.trim()}\n\n")
            append("------------------------\n\n")
        }
    }

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Minhas receitas do Chef IA")
        putExtra(Intent.EXTRA_TEXT, shareText)
    }

    context.startActivity(
        Intent.createChooser(
            intent,
            "Compartilhar receitas via"
        )
    )
}

