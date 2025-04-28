package com.elian.calculadora_empleado_vitivinicola_trivento.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.elian.calculadora_empleado_vitivinicola_trivento.R
import com.elian.calculadora_empleado_vitivinicola_trivento.model.categorias
import com.elian.calculadora_empleado_vitivinicola_trivento.model.escalasAntiguedad
import com.elian.calculadora_empleado_vitivinicola_trivento.ui.theme.CalculadoraSalarioTheme
import com.elian.calculadora_empleado_vitivinicola_trivento.viewmodel.SalarioViewModel
import java.text.NumberFormat
import java.util.Locale

/**
 * Formatea un valor Double como moneda usando la configuración regional Argentina (AR$).
 * Ejemplo: 12345.67 -> $ 12.345,67
 */
fun formatCurrency(value: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "AR"))
    return format.format(value)
}

/**
 * Filtra un String para devolver solo los caracteres que son dígitos.
 * Se usa en onValueChange de los TextFields de horas extras.
 */
fun filterDigits(text: String): String {
    return text.filter { it.isDigit() }
}

@OptIn(ExperimentalMaterial3Api::class) // Necesario para ExposedDropdownMenuBox y otros componentes M3
@Composable
fun SalarioScreen(viewModel: SalarioViewModel = viewModel()) {
    // --- Estados de la UI ---
    var categoriaSeleccionada by remember { mutableStateOf(categorias.first()) } // Estado para la categoría seleccionada
    var antiguedadIndexSeleccionado by remember { mutableIntStateOf(0) } // Estado para el índice de antigüedad seleccionado
    var horasExtra100Text by remember { mutableStateOf("") } // Estado para el texto del input Horas 100%
    var horasExtra50Text by remember { mutableStateOf("") } // Estado para el texto del input Horas 50%
    var expandedCategoria by remember { mutableStateOf(false) } // Estado para controlar si el menú de categoría está abierto
    var expandedAntiguedad by remember { mutableStateOf(false) } // Estado para controlar si el menú de antigüedad está abierto

    // Validación simple para habilitar/deshabilitar el botón Calcular
    // Se asegura que los campos de horas estén vacíos o contengan solo números.
    val horas100Validas = horasExtra100Text.all { it.isDigit() }
    val horas50Validas = horasExtra50Text.all { it.isDigit() }
    val canCalculate = horas100Validas && horas50Validas // El botón se habilita si ambos son válidos

    // Recolectar el estado del desglose del salario desde el ViewModel
    val salarioInfo by viewModel.salarioBreakdown.collectAsState()
    val scrollState = rememberScrollState() // Estado para controlar el scroll vertical de la columna

    CalculadoraSalarioTheme {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(stringResource(R.string.app_title), style = MaterialTheme.typography.titleLarge) }
                    // Se podría añadir un subtítulo si fuera necesario:
                    // subtitle = { Text("Convenio Vitivinícola") }
                )
            }
        ) { paddingValues -> // Recibe los paddings del Scaffold (para TopAppBar, etc.)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues) // Aplicar padding del Scaffold
                    .padding(horizontal = 16.dp) // Añadir padding horizontal propio
                    .verticalScroll(scrollState) // Hacer la columna desplazable si el contenido excede la pantalla
            ) {
                Spacer(modifier = Modifier.height(16.dp)) // Espacio inicial

                // --- Sección de Entradas de Datos ---
                Text(stringResource(R.string.section_inputs), style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                // ComboBox para Categoría (Usando ExposedDropdownMenuBox recomendado en M3)
                ExposedDropdownMenuBox(
                    expanded = expandedCategoria,
                    onExpandedChange = { expandedCategoria = !expandedCategoria },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = categoriaSeleccionada.nombre, // Muestra el nombre de la categoría seleccionada
                        onValueChange = {}, // No se cambia escribiendo
                        readOnly = true, // El campo es de solo lectura
                        label = { Text(stringResource(R.string.label_category)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoria) }, // Icono de flecha
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor() // Ancla el menú al TextField
                    )
                    // Menú desplegable con las opciones de categoría
                    ExposedDropdownMenu(
                        expanded = expandedCategoria,
                        onDismissRequest = { expandedCategoria = false } // Cierra el menú si se toca fuera
                    ) {
                        categorias.forEach { categoria ->
                            DropdownMenuItem(
                                text = { Text(categoria.nombre) }, // Texto de cada opción
                                onClick = {
                                    categoriaSeleccionada = categoria // Actualiza el estado
                                    expandedCategoria = false // Cierra el menú
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp)) // Espacio entre campos

                // ComboBox para Antigüedad
                ExposedDropdownMenuBox(
                    expanded = expandedAntiguedad,
                    onExpandedChange = { expandedAntiguedad = !expandedAntiguedad },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        // Muestra los años correspondientes al índice seleccionado
                        value = stringResource(R.string.years_template, antiguedadIndexSeleccionado * 3),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.label_seniority)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedAntiguedad) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedAntiguedad,
                        onDismissRequest = { expandedAntiguedad = false }
                    ) {
                        // Genera las opciones del menú basadas en los índices de `escalasAntiguedad`
                        escalasAntiguedad.indices.forEach { index ->
                            DropdownMenuItem(
                                // Muestra los años para cada opción
                                text = { Text(stringResource(R.string.years_template, index * 3)) },
                                onClick = {
                                    antiguedadIndexSeleccionado = index // Actualiza el índice seleccionado
                                    expandedAntiguedad = false // Cierra el menú
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Campo de texto para Horas Extras al 100%
                OutlinedTextField(
                    value = horasExtra100Text,
                    onValueChange = { horasExtra100Text = filterDigits(it) }, // Aplica el filtro para permitir solo dígitos
                    label = { Text(stringResource(R.string.label_overtime_100)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), // Teclado numérico simple
                    singleLine = true, // Campo de una sola línea
                    // Muestra estado de error si el campo no está vacío Y no contiene solo dígitos
                    isError = !horas100Validas && horasExtra100Text.isNotEmpty()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Campo de texto para Horas Extras al 50%
                OutlinedTextField(
                    value = horasExtra50Text,
                    onValueChange = { horasExtra50Text = filterDigits(it) }, // Aplica el filtro
                    label = { Text(stringResource(R.string.label_overtime_50)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = !horas50Validas && horasExtra50Text.isNotEmpty()
                )

                Spacer(modifier = Modifier.height(24.dp)) // Mayor espacio antes del botón

                // Botón para ejecutar el cálculo
                Button(
                    onClick = {
                        // Convierte el texto filtrado a Int (o 0 si está vacío) y llama al ViewModel
                        val horasExtra100 = horasExtra100Text.toIntOrNull() ?: 0
                        val horasExtra50 = horasExtra50Text.toIntOrNull() ?: 0
                        viewModel.calcularSalario(
                            categoria = categoriaSeleccionada,
                            antiguedadIndex = antiguedadIndexSeleccionado,
                            horasExtra100 = horasExtra100,
                            horasExtra50 = horasExtra50
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = canCalculate // El botón está habilitado solo si las entradas de horas son válidas
                ) {
                    Text(stringResource(R.string.button_calculate))
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- Sección de Resultados (Desglose) ---
                // Solo se muestra si se ha calculado un salario (salarioFinalNeto > 0)
                if (salarioInfo.salarioFinalNeto > 0.0 || horasExtra100Text.isNotEmpty() || horasExtra50Text.isNotEmpty() || salarioInfo.subtotalBrutoRemunerativo > 0.0 ) { // Condición para mostrar la sección
                    Text(stringResource(R.string.section_results), style = MaterialTheme.typography.titleMedium)
                    Divider(modifier = Modifier.padding(vertical = 8.dp)) // Línea separadora

                    // Muestra cada componente del desglose usando el Composable auxiliar `BreakdownItem`
                    BreakdownItem(label = stringResource(R.string.result_base_salary), value = salarioInfo.salarioBaseCalculado)
                    BreakdownItem(label = stringResource(R.string.result_seniority_bonus), value = salarioInfo.adicionalAntiguedad)
                    BreakdownItem(label = stringResource(R.string.result_presenteeism), value = salarioInfo.adicionalPresentismo)
                    BreakdownItem(label = stringResource(R.string.result_art4), value = salarioInfo.adicionalCompAnualArt4)
                    BreakdownItem(label = stringResource(R.string.result_trivento1), value = salarioInfo.adicionalIncentivoTrivento)
                    BreakdownItem(label = stringResource(R.string.result_trivento2), value = salarioInfo.adicionalIncentivoTrivento2)
                    // Subtotal Bruto Remunerativo (marcado como subtotal para posible estilo diferente)
                    BreakdownItem(label = stringResource(R.string.result_gross_subtotal), value = salarioInfo.subtotalBrutoRemunerativo, isSubtotal = true)

                    Spacer(modifier = Modifier.height(8.dp)) // Espacio antes de los descuentos

                    // Descuentos (se muestran como negativos usando -valor)
                    BreakdownItem(label = stringResource(R.string.result_deduction_solidarity), value = -salarioInfo.descuentoAporteSolidario)
                    BreakdownItem(label = stringResource(R.string.result_deduction_law), value = -salarioInfo.descuentoJubilacionLey)
                    BreakdownItem(label = stringResource(R.string.result_deduction_funeral), value = -salarioInfo.descuentoSepelio)

                    // Subtotal Neto Remunerativo
                    BreakdownItem(label = stringResource(R.string.result_net_rem_subtotal), value = salarioInfo.subtotalNetoRemunerativo, isSubtotal = true)

                    Spacer(modifier = Modifier.height(8.dp)) // Espacio antes de no remunerativos

                    // Adicionales No Remunerativos y Extras
                    BreakdownItem(label = stringResource(R.string.result_non_remunerative), value = salarioInfo.adicionalNoRemunerativo)
                    BreakdownItem(label = stringResource(R.string.result_meal_allowance), value = salarioInfo.adicionalRefrigerio) // Refrigerio
                    BreakdownItem(label = stringResource(R.string.result_overtime_50), value = salarioInfo.pagoExtra50)
                    BreakdownItem(label = stringResource(R.string.result_overtime_100), value = salarioInfo.pagoExtra100)

                    Divider(modifier = Modifier.padding(vertical = 8.dp)) // Línea antes del total

                    // --- Resultado Final Neto ---
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween // Alinea texto a la izquierda y valor a la derecha
                    ) {
                        Text(stringResource(R.string.result_final_net), style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = formatCurrency(salarioInfo.salarioFinalNeto), // Muestra el valor formateado como moneda
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.End // Alinea el valor a la derecha
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp)) // Espacio después del resultado final
                }

                // --- Footer con el Autor ---
                // El Spacer con weight(1f) empuja este Text hacia el fondo de la columna
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = stringResource(R.string.author_name), // Usa el recurso de string para el nombre
                    style = MaterialTheme.typography.bodySmall, // Estilo pequeño para el footer
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally) // Centra el texto horizontalmente
                        .padding(bottom = 8.dp) // Añade un pequeño padding inferior
                )
            }
        }
    }
}

/**
 * Composable auxiliar reutilizable para mostrar una línea del desglose del salario.
 * Muestra una etiqueta a la izquierda y un valor formateado como moneda a la derecha.
 * @param label El texto descriptivo de la línea (ej. "Salario Básico").
 * @param value El valor numérico a mostrar.
 * @param isSubtotal Indica si esta línea representa un subtotal (para aplicar un estilo diferente si se desea).
 */
@Composable
fun BreakdownItem(label: String, value: Double, isSubtotal: Boolean = false) {
    // Se podría usar un estilo diferente si es un subtotal
    val textStyle = if (isSubtotal) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodyMedium
    val valueColor = if (value < 0) MaterialTheme.colorScheme.error else LocalContentColor.current // Color rojo si es negativo (descuento)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp), // Espacio vertical entre ítems
        horizontalArrangement = Arrangement.SpaceBetween // Etiqueta a la izquierda, valor a la derecha
    ) {
        Text(text = label, style = textStyle)
        Text(
            text = formatCurrency(value), // Formatea el valor como moneda
            style = textStyle,
            color = valueColor, // Aplica color de error si es negativo
            textAlign = TextAlign.End // Alinea el valor a la derecha
        )
    }
}