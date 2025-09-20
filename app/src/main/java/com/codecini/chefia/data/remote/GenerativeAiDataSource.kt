package com.codecini.chefia.data.remote

import com.codecini.chefia.BuildConfig
import com.codecini.chefia.domain.model.Recipe
import com.codecini.chefia.domain.model.RecipeGenerationMode
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GenerativeAiDataSource @Inject constructor() {

    private val config = generationConfig {
        temperature = 0.7f
        topK = 50
    }

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY,
        generationConfig = config
    )

    suspend fun generateRecipe(ingredients: String, mode: RecipeGenerationMode): Result<Recipe> {
        return try {
            val prompt = createPrompt(ingredients, mode)
            val response = generativeModel.generateContent(prompt)
            val parsedRecipe = parseResponse(response.text ?: "")
            Result.success(parsedRecipe)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun createPrompt(ingredients: String, mode: RecipeGenerationMode): String {
        val modeInstruction = when (mode) {
            RecipeGenerationMode.STRICT -> "Use APENAS os ingredientes fornecidos. Não invente ingredientes principais."
            RecipeGenerationMode.CREATIVE -> "Você pode sugerir 1 ou 2 ingredientes simples e comuns que combinem bem para melhorar a receita."
            RecipeGenerationMode.HEALTHY -> "Crie a versão mais saudável possível da receita, priorizando métodos de cozimento leves (grelhar, assar) e evitando excesso de gordura e açúcar."
            RecipeGenerationMode.QUICK -> "Crie a receita mais rápida possível, com tempo total de preparo de no máximo 20 minutos."
        }

        return """
        Você é um assistente de culinária criativo chamado "Chef IA".
        Sua missão é criar uma receita deliciosa e fácil de seguir com base nos ingredientes que o usuário possui e na preferência de estilo.

        **Regras:**
        1. $modeInstruction
        2. Você pode assumir que o usuário possui itens básicos como sal, pimenta, azeite e água.
        3. A resposta deve ser formatada EXATAMENTE em Markdown da seguinte forma, usando ":::" como separador:

        [Título Criativo da Receita]
        :::
        - [Quantidade] de [Ingrediente 1]
        - [Quantidade] de [Ingrediente 2]
        :::
        1. [Primeiro passo]
        2. [Segundo passo]
        3. [Terceiro passo]

        **Ingredientes Fornecidos:**
        $ingredients
        """.trimIndent()
    }

    private fun parseResponse(responseText: String): Recipe {
        val parts = responseText.split(":::")
        if (parts.size < 3) {
            throw IllegalStateException("Formato de resposta inesperado da IA.")
        }
        return Recipe(
            title = parts[0].trim(),
            ingredients = parts[1].trim(),
            instructions = parts[2].trim()
        )
    }
}

