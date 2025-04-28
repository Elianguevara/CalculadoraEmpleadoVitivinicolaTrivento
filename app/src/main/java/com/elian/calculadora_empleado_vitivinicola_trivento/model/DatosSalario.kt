package com.elian.calculadora_empleado_vitivinicola_trivento.model

// Lista predefinida de categorías con sus factores multiplicadores
val categorias = listOf(
    Categoria("Obrero Común", 1.0),
    Categoria("Obrero Especializado", 1.05),
    Categoria("Obrero con Oficio", 1.10),
    Categoria("Tractorista / Chofer", 1.15),
    Categoria("Injertador / Parralero", 1.20),
    Categoria("Mecánico", 1.25)
)

// Lista de factores de antigüedad (cada índice corresponde a un tramo de años)
// Índice 0 = 0 años (factor 1.00)
// Índice 1 = 3 años (factor 1.025)
// Índice 2 = 6 años (factor 1.05)
// ... y así sucesivamente, asumiendo tramos de 3 años.
val escalasAntiguedad = listOf(
    1.00, 1.025, 1.05, 1.075, 1.10, 1.125, 1.15, 1.175, 1.20, 1.225, 1.25
    // Puedes añadir más factores si hay más tramos de antigüedad
)
