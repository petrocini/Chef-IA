package com.codecini.chefia.ui.features.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.codecini.chefia.R
import com.codecini.chefia.ui.components.ConfirmationDialog
import com.codecini.chefia.ui.components.EmptyState
import com.codecini.chefia.ui.components.RecipeCard
import com.codecini.chefia.ui.navigation.Screen
import com.codecini.chefia.ui.util.shareMultipleRecipes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        val selectionCount = uiState.selectedRecipesIds.size
        ConfirmationDialog(
            onDismissRequest = { showDeleteDialog = false },
            onConfirmation = {
                viewModel.deleteSelectedRecipes()
                showDeleteDialog = false
            },
            dialogTitle = stringResource(R.string.dialog_delete_recipes_title),
            dialogText = pluralStringResource(
                R.plurals.dialog_delete_recipes_description,
                count = selectionCount,
                selectionCount
            ),
            confirmButtonText = stringResource(R.string.action_delete)
        )
    }

    Scaffold(
        topBar = {
            AnimatedVisibility(
                visible = uiState.isSelectionMode,
                enter = slideInVertically(initialOffsetY = { -it }),
                exit = slideOutVertically(targetOffsetY = { -it })
            ) {
                TopAppBar(
                    title = {
                        Text(
                            pluralStringResource(
                                R.plurals.home_selection_count,
                                count = uiState.selectedRecipesIds.size,
                                uiState.selectedRecipesIds.size
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = viewModel::clearSelection) {
                            Icon(Icons.Default.Close, contentDescription = stringResource(R.string.action_close))
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            val selectedRecipes = uiState.recipes.filter { recipe ->
                                uiState.selectedRecipesIds.contains(recipe.id)
                            }
                            if (selectedRecipes.isNotEmpty()) {
                                shareMultipleRecipes(context, selectedRecipes)
                            }
                        }) {
                            Icon(Icons.Default.Share, contentDescription = stringResource(R.string.action_share))
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.action_delete))
                        }
                    }
                )
            }
            AnimatedVisibility(
                visible = !uiState.isSelectionMode,
                enter = slideInVertically(initialOffsetY = { -it }),
                exit = slideOutVertically(targetOffsetY = { -it })
            ) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            stringResource(R.string.app_name),
                            style = MaterialTheme.typography.displaySmall,
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = Color.White
                    )
                )
            }
        },
        floatingActionButton = {
            if (!uiState.isSelectionMode) {
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.Generate.route) },
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.home_fab_generate_recipe))
                }
            }
        }
    ) { paddingValues ->
        if (uiState.recipes.isEmpty()) {
            EmptyState(
                modifier = Modifier.padding(paddingValues),
                message = stringResource(R.string.home_empty_title),
                primaryActionText = stringResource(R.string.home_empty_primary_action),
                onPrimaryActionClick = { navController.navigate(Screen.Generate.route) },
                secondaryActionText = stringResource(R.string.home_empty_secondary_action),
                onSecondaryActionClick = { navController.navigate(Screen.Onboarding.route) }
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.recipes, key = { it.id }) { recipe ->
                    RecipeCard(
                        recipe = recipe,
                        isSelected = uiState.selectedRecipesIds.contains(recipe.id),
                        onClick = {
                            if (uiState.isSelectionMode) {
                                viewModel.onRecipeClick(recipe)
                            } else {
                                recipe.id.let { id ->
                                    navController.navigate(Screen.RecipeDetail.createRoute(id))
                                }
                            }
                        },
                        onLongClick = { viewModel.onRecipeLongClick(recipe) }
                    )
                }
            }
        }
    }
}

