package com.codecini.chefia.domain.model

data class Recipe(
    val id: Long = 0,
    val title: String,
    val ingredients: String,
    val instructions: String
)
