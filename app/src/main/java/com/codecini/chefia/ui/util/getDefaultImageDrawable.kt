package com.codecini.chefia.ui.util

import androidx.annotation.DrawableRes
import com.codecini.chefia.R
import com.codecini.chefia.domain.model.Recipe

@DrawableRes
fun getDefaultImageDrawable(recipe: Recipe): Int {
    val titleAndIngredients = "${recipe.title.lowercase()} ${recipe.ingredients.lowercase()}"

    return when {
        "frango" in titleAndIngredients -> R.drawable.chicken
        "carne" in titleAndIngredients || "bife" in titleAndIngredients || "bovina" in titleAndIngredients -> R.drawable.beef
        "peixe" in titleAndIngredients || "salmão" in titleAndIngredients || "tilápia" in titleAndIngredients -> R.drawable.fish
        "massa" in titleAndIngredients || "macarrão" in titleAndIngredients || "pasta" in titleAndIngredients || "nhoque" in titleAndIngredients -> R.drawable.pasta
        "bolo" in titleAndIngredients || "torta" in titleAndIngredients -> R.drawable.cake
        "salada" in titleAndIngredients || "vegetais" in titleAndIngredients || "legumes" in titleAndIngredients -> R.drawable.salad
        "sopa" in titleAndIngredients || "caldo" in titleAndIngredients -> R.drawable.soup
        "doce" in titleAndIngredients || "sobremesa" in titleAndIngredients || "brigadeiro" in titleAndIngredients -> R.drawable.candy
        else -> R.drawable.standard_food
    }
}

