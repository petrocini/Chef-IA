package com.codecini.chefia.ui.navigation

sealed class Screen(val route: String) {
    data object Onboarding : Screen("onboarding")
    data object Home : Screen("home")
    data object Generate : Screen("generate")
    data object RecipeDetail : Screen("recipe_detail/{recipeId}?title={title}&ingredients={ingredients}&instructions={instructions}") {
        fun createRoute(recipeId: Long): String = "recipe_detail/$recipeId"
        fun createRouteForNewRecipe(
            title: String,
            ingredients: String,
            instructions: String
        ): String = "recipe_detail/-1?title=$title&ingredients=$ingredients&instructions=$instructions"
    }
}

