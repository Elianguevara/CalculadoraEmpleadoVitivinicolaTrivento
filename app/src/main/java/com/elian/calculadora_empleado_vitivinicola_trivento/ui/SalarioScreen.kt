package com.elian.calculadora_empleado_vitivinicola_trivento.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider // <-- CAMBIO AQUÍ
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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

// ... (Las funciones formatCurrency y filterDigits no cambian)
fun formatCurrency(value: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "AR"))
    return format.format(value)
}
fun filterDigits(text: String): String {
    return text.filter { it.isDigit() }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalarioScreen(viewModel: SalarioViewModel = viewModel()) {
    // --- Estados de la UI (sin cambios) ---
    var categoriaSeleccionada by remember { mutableStateOf(categorias.first()) }
    var antiguedadIndexSeleccionado by remember { mutableIntStateOf(0) }
    var horasExtra100Text by remember { mutableStateOf("") }
    var horasExtra50Text by remember { mutableStateOf("") }
    var expandedCategoria by remember { mutableStateOf(false) }
    var expandedAntiguedad by remember { mutableStateOf(false) }

    val horas100Validas = horasExtra100Text.all { it.isDigit() }
    val horas50Validas = horasExtra50Text.all { it.isDigit() }
    val canCalculate = horas100Validas && horas50Validas

    val salarioInfo by viewModel.salarioBreakdown.collectAsState()
    val scrollState = rememberScrollState()

    CalculadoraSalarioTheme {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(stringResource(R.string.app_title))
                            Text(
                                text = stringResource(R.string.app_subtitle),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(scrollState)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(stringResource(R.string.section_inputs), style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                // ComboBox para Categoría
                ExposedDropdownMenuBox(
                    expanded = expandedCategoria,
                    onExpandedChange = { expandedCategoria = !expandedCategoria },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = categoriaSeleccionada.nombre,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.label_category)) },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Work, contentDescription = "Categoría")
                        },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoria) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            focusedLeadingIconColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCategoria,
                        onDismissRequest = { expandedCategoria = false },
                        modifier = Modifier.heightIn(max = 280.dp)
                    ) {
                        Surface(color = MaterialTheme.colorScheme.primaryContainer) {
                            Column {
                                categorias.forEachIndexed { index, categoria ->
                                    DropdownMenuItem(
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Work,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        },
                                        text = {
                                            Text(
                                                text = categoria.nombre,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        },
                                        onClick = {
                                            categoriaSeleccionada = categoria
                                            expandedCategoria = false
                                        }
                                    )
                                    if (index < categorias.lastIndex) {
                                        HorizontalDivider( // <-- CAMBIO AQUÍ
                                            modifier = Modifier.padding(horizontal = 16.dp),
                                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ComboBox para Antigüedad
                ExposedDropdownMenuBox(
                    expanded = expandedAntiguedad,
                    onExpandedChange = { expandedAntiguedad = !expandedAntiguedad },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = stringResource(R.string.years_template, antiguedadIndexSeleccionado * 3),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.label_seniority)) },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.CalendarToday, contentDescription = "Antigüedad")
                        },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedAntiguedad) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            focusedLeadingIconColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandedAntiguedad,
                        onDismissRequest = { expandedAntiguedad = false },
                        modifier = Modifier.heightIn(max = 280.dp)
                    ) {
                        Surface(color = MaterialTheme.colorScheme.primaryContainer) {
                            Column {
                                escalasAntiguedad.indices.forEach { index ->
                                    DropdownMenuItem(
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.CalendarToday,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        },
                                        text = {
                                            Text(
                                                stringResource(R.string.years_template, index * 3),
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        },
                                        onClick = {
                                            antiguedadIndexSeleccionado = index
                                            expandedAntiguedad = false
                                        }
                                    )
                                    if (index < escalasAntiguedad.indices.last) {
                                        HorizontalDivider( // <-- CAMBIO AQUÍ
                                            modifier = Modifier.padding(horizontal = 16.dp),
                                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // ... (El resto del código sigue exactamente igual, pero con HorizontalDivider)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = horasExtra100Text,
                    onValueChange = { horasExtra100Text = filterDigits(it) },
                    label = { Text(stringResource(R.string.label_overtime_100)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = !horas100Validas && horasExtra100Text.isNotEmpty()
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = horasExtra50Text,
                    onValueChange = { horasExtra50Text = filterDigits(it) },
                    label = { Text(stringResource(R.string.label_overtime_50)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = !horas50Validas && horasExtra50Text.isNotEmpty()
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
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
                    enabled = canCalculate
                ) {
                    Text(stringResource(R.string.button_calculate))
                }
                Spacer(modifier = Modifier.height(24.dp))
                if (salarioInfo.salarioFinalNeto > 0.0 || horasExtra100Text.isNotEmpty() || horasExtra50Text.isNotEmpty() || salarioInfo.subtotalBrutoRemunerativo > 0.0 ) {
                    Text(stringResource(R.string.section_results), style = MaterialTheme.typography.titleMedium)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) // <-- CAMBIO AQUÍ

                    BreakdownItem(label = stringResource(R.string.result_base_salary), value = salarioInfo.salarioBaseCalculado)
                    BreakdownItem(label = stringResource(R.string.result_seniority_bonus), value = salarioInfo.adicionalAntiguedad)
                    BreakdownItem(label = stringResource(R.string.result_presenteeism), value = salarioInfo.adicionalPresentismo)
                    BreakdownItem(label = stringResource(R.string.result_art4), value = salarioInfo.adicionalCompAnualArt4)
                    BreakdownItem(label = stringResource(R.string.result_trivento1), value = salarioInfo.adicionalIncentivoTrivento)
                    BreakdownItem(label = stringResource(R.string.result_trivento2), value = salarioInfo.adicionalIncentivoTrivento2)
                    BreakdownItem(label = stringResource(R.string.result_gross_subtotal), value = salarioInfo.subtotalBrutoRemunerativo, isSubtotal = true)
                    Spacer(modifier = Modifier.height(8.dp))
                    BreakdownItem(label = stringResource(R.string.result_deduction_solidarity), value = -salarioInfo.descuentoAporteSolidario)
                    BreakdownItem(label = stringResource(R.string.result_deduction_law), value = -salarioInfo.descuentoJubilacionLey)
                    BreakdownItem(label = stringResource(R.string.result_deduction_funeral), value = -salarioInfo.descuentoSepelio)
                    BreakdownItem(label = stringResource(R.string.result_net_rem_subtotal), value = salarioInfo.subtotalNetoRemunerativo, isSubtotal = true)
                    Spacer(modifier = Modifier.height(8.dp))
                    BreakdownItem(label = stringResource(R.string.result_non_remunerative), value = salarioInfo.adicionalNoRemunerativo)
                    BreakdownItem(label = stringResource(R.string.result_meal_allowance), value = salarioInfo.adicionalRefrigerio)
                    BreakdownItem(label = stringResource(R.string.result_overtime_50), value = salarioInfo.pagoExtra50)
                    BreakdownItem(label = "  ↳ de bolsillo", value = salarioInfo.pagoExtra50Neto)
                    BreakdownItem(label = stringResource(R.string.result_overtime_100), value = salarioInfo.pagoExtra100)
                    BreakdownItem(label = "  ↳ de bolsillo", value = salarioInfo.pagoExtra100Neto)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) // <-- CAMBIO AQUÍ

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(stringResource(R.string.result_final_net), style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = formatCurrency(salarioInfo.salarioFinalNeto),
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.End
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = stringResource(R.string.author_name),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 8.dp)
                )
            }
        }
    }
}

// ... (El Composable BreakdownItem no cambia)
@Composable
fun BreakdownItem(label: String, value: Double, isSubtotal: Boolean = false) {
    val textStyle = if (isSubtotal) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodyMedium
    val valueColor = if (value < 0) MaterialTheme.colorScheme.error else LocalContentColor.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = textStyle)
        Text(
            text = formatCurrency(value),
            style = textStyle,
            color = valueColor,
            textAlign = TextAlign.End
        )
    }
}