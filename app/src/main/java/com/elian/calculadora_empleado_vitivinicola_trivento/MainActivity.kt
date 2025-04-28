package com.elian.calculadora_empleado_vitivinicola_trivento

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.elian.calculadora_empleado_vitivinicola_trivento.ui.SalarioScreen // Importa la pantalla principal
import com.elian.calculadora_empleado_vitivinicola_trivento.ui.theme.CalculadoraSalarioTheme // Importa el tema de la app
import dagger.hilt.android.AndroidEntryPoint // Necesario si usas Hilt para la inyección del ViewModel

@AndroidEntryPoint // Anotación necesaria para Hilt
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { // Establece el contenido de la actividad usando Jetpack Compose
            CalculadoraSalarioTheme { // Aplica el tema definido
                // Llama al Composable principal que contiene la UI de la calculadora
                SalarioScreen()
            }
        }
    }
}