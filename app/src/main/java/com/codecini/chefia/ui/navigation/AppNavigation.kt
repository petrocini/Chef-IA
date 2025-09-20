package com.codecini.chefia.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.codecini.chefia.StartDestination
import com.codecini.chefia.ui.features.details.RecipeDetailScreen
import com.codecini.chefia.ui.features.generate.GenerateRecipeScreen
import com.codecini.chefia.ui.features.home.HomeScreen
import com.codecini.chefia.ui.features.onboarding.OnboardingScreen

@Composable
fun AppNavigation(startDestination: StartDestination) {
    val navController = rememberNavController()

    val initialRoute = when (startDestination) {
        is StartDestination.Home -> Screen.Home.route
        is StartDestination.Onboarding -> Screen.Onboarding.route
        else -> ""
    }

    NavHost(navController = navController, startDestination = initialRoute) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(navController = navController)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.Generate.route) {
            GenerateRecipeScreen(navController = navController)
        }
        composable(
            route = Screen.RecipeDetail.route,
            arguments = listOf(
                navArgument("recipeId") { type = NavType.LongType },
                navArgument("title") {
                    type = NavType.StringType
                    nullable = true
                },
                navArgument("ingredients") {
                    type = NavType.StringType
                    nullable = true
                },
                navArgument("instructions") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) {
            RecipeDetailScreen(navController = navController)
        }
    }
}

