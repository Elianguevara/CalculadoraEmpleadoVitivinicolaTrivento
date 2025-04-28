package com.elian.calculadora_empleado_vitivinicola_trivento.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable


private val LightColors = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
fun CalculadoraSalarioTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography,
        content = content
    )
}
