package com.codecini.chefia.domain.model

enum class RecipeGenerationMode(val displayText: String, val description: String) {
    STRICT(
        displayText = "Só o que tenho",
        description = "A IA usará apenas os ingredientes que você listou."
    ),

    CREATIVE(
        displayText = "Toque do Chef",
        description = "Permite que a IA sugira 1 ou 2 ingredientes extras para aprimorar a receita."
    ),

    HEALTHY(
        displayText = "Versão Light",
        description = "A IA irá focar em métodos de cozimento e ingredientes mais saudáveis."
    ),

    QUICK(
        displayText = "Rápida (20 min)",
        description = "Gera uma receita otimizada para ser preparada em até 20 minutos."
    )
}

