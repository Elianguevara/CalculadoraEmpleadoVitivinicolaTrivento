package com.elian.calculadora_empleado_vitivinicola_trivento.viewmodel

import androidx.lifecycle.ViewModel
import com.elian.calculadora_empleado_vitivinicola_trivento.model.Categoria
import com.elian.calculadora_empleado_vitivinicola_trivento.model.escalasAntiguedad
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.math.max // Para evitar valores negativos

/**
 * Data class para almacenar el desglose detallado del cálculo del salario.
 * Permite mostrar cada componente en la UI para mayor transparencia.
 */
data class SalarioBreakdown(
    val salarioBaseCalculado: Double = 0.0,        // Salario básico (cat * antig)
    val adicionalPresentismo: Double = 0.0,        // Monto adicional por presentismo
    val adicionalAntiguedad: Double = 0.0,         // Monto adicional por antigüedad (diferencia sobre base)
    val adicionalCompAnualArt4: Double = 0.0,      // Monto adicional Art. 4 CCT
    val adicionalIncentivoTrivento: Double = 0.0,  // Monto adicional Incentivo 1 Trivento
    val adicionalIncentivoTrivento2: Double = 0.0, // Monto adicional Incentivo 2 Trivento
    val subtotalBrutoRemunerativo: Double = 0.0,   // Suma de conceptos remunerativos brutos
    val descuentoSepelio: Double = 0.0,            // Descuento por subsidio de sepelio
    val descuentoAporteSolidario: Double = 0.0,    // Descuento por aporte solidario (CCT)
    val descuentoJubilacionLey: Double = 0.0,      // Descuentos de ley (Jubilación + Ley 19032 + Obra Social)
    val subtotalNetoRemunerativo: Double = 0.0,    // Bruto remunerativo menos descuentos de ley
    val adicionalNoRemunerativo: Double = 0.0,     // Monto adicional no remunerativo (CCT)
    val adicionalRefrigerio: Double = 0.0,         // Monto adicional por refrigerio (CCT)
    val pagoExtra50: Double = 0.0,                 // Monto a pagar por horas extras al 50%
    val pagoExtra100: Double = 0.0,                // Monto a pagar por horas extras al 100%
    val salarioFinalNeto: Double = 0.0             // El monto final de bolsillo
)

@HiltViewModel
class SalarioViewModel @Inject constructor() : ViewModel() {


    // --- Constantes de Cálculo ---
    // Es FUNDAMENTAL verificar estos valores contra el Convenio Colectivo de Trabajo (CCT) vigente.
    private companion object {
        // Valores Base y Fijos (¡Actualizar según CCT y acuerdos paritarios!)
        const val SALARIO_BASE_OFICIAL = 377350.0        // Salario básico de referencia (Obrero Común sin antigüedad)
        const val ADICIONAL_NO_REMUNERATIVO = 170806.0   // Adicional No Remunerativo según CCT
        const val REFRIGERIO = 132497.0                // Adicional por Refrigerio según CCT
        const val ADICIONAL_INCENTIVO_TRIVENTO_1 = 25000.0 // Adicional específico Trivento 1
        const val ADICIONAL_INCENTIVO_TRIVENTO_2 = 30000.0 // Adicional específico Trivento 2

        // Porcentajes (¡Verificar base de cálculo para cada uno según CCT!)
        const val PRESENTISMO_PORCENTAJE = 0.05         // 5% sobre SALARIO_BASE_OFICIAL
        const val COMP_ANUAL_ART4_PORCENTAJE = 0.0532   // 5.32% sobre (SALARIO_BASE_OFICIAL * categoria.factor) - Confirmar base CCT
        const val DESCUENTO_SEPELIO_PORCENTAJE = 0.021545  // 1.6% sobre SALARIO_BASE_OFICIAL - Confirmar base CCT
        const val APORTE_SOLIDARIO_PORCENTAJE = 0.015   // 1.5% sobre (SALARIO_BASE_OFICIAL * categoria.factor) - Confirmar base CCT

        // Descuentos de Ley Estándar (¡Estos son generales, verificar si CCT o situación particular aplica otros!)
        const val DESCUENTO_JUBILACION_PORCENTAJE = 0.11    // 11% sobre Bruto Remunerativo
        const val DESCUENTO_LEY_19032_PORCENTAJE = 0.03     // 3% sobre Bruto Remunerativo (INSSJP - PAMI)
        const val DESCUENTO_OBRA_SOCIAL_PORCENTAJE = 0.03   // 3% sobre Bruto Remunerativo

        /* NOTA IMPORTANTE SOBRE DESCUENTOS:
           Tu código original tenía un `descuentoFinal = 0.17` (17%) aplicado sobre un subtotal.
           Esto es INUSUAL como descuento de ley estándar en Argentina. Los descuentos habituales son los detallados arriba
           (Jubilación 11%, Ley 19032 3%, Obra Social 3% = 17% TOTAL sobre REMUNERATIVO BRUTO).
           He reemplazado tu 17% fijo por estos descuentos estándar.
           *** DEBES VALIDAR ESTO ***: ¿El 17% era una simplificación correcta o los descuentos deben calcularse individualmente sobre el bruto remunerativo?
           ¿Hay algún otro descuento específico del CCT vitivinícola o de Trivento?
        */

        // Cálculo Horas/Jornal (Según CCT, usualmente 200hs mensuales / 25 jornales)
        const val DIAS_MES_JORNAL = 25.0 // Días teóricos para calcular jornal
        const val HORAS_JORNAL = 8.0     // Horas por jornal
        const val FACTOR_EXTRA_50 = 1.5  // Multiplicador para hora extra al 50%
        const val FACTOR_EXTRA_100 = 2.0 // Multiplicador para hora extra al 100%
    }

    // StateFlow para exponer el desglose del salario a la UI
    private val _salarioBreakdown = MutableStateFlow(SalarioBreakdown()) // Inicializa con valores en 0.0
    val salarioBreakdown: StateFlow<SalarioBreakdown> = _salarioBreakdown.asStateFlow()

    /**
     * Calcula el salario detallado (bruto, descuentos, netos, extras) basado en los parámetros.
     * Actualiza el StateFlow `_salarioBreakdown` con el resultado completo.
     *
     * @param categoria Objeto Categoria del empleado.
     * @param antiguedadIndex Índice seleccionado en la lista `escalasAntiguedad`.
     * @param horasExtra100 Cantidad de horas extras al 100%.
     * @param horasExtra50 Cantidad de horas extras al 50%.
     */
    fun calcularSalario(
        categoria: Categoria,
        antiguedadIndex: Int,
        horasExtra100: Int,
        horasExtra50: Int
    ) {
        // --- Validaciones y Obtención de Factores ---
        val factorAntiguedad = escalasAntiguedad.getOrNull(antiguedadIndex) ?: 1.0 // Factor de antigüedad, default 1.0 si índice es inválido
        val salarioBasicoCategoria = SALARIO_BASE_OFICIAL * categoria.factor // Salario base según categoría (sin antigüedad)

        // --- Cálculo de Componentes Remunerativos Brutos ---
        // 1. Salario Base Calculado: Afectado por categoría y antigüedad.
        val baseConAntiguedad = salarioBasicoCategoria * factorAntiguedad
        // 2. Adicional por Antigüedad: La diferencia que agrega la antigüedad sobre el base de categoría.
        val adicionalAntiguedadMonto = baseConAntiguedad - salarioBasicoCategoria
        // 3. Adicional por Presentismo: Calculado sobre el salario base oficial (sin categoría ni antigüedad). ¡Verificar base CCT!
        val adicionalPresentismoMonto = SALARIO_BASE_OFICIAL * PRESENTISMO_PORCENTAJE
        // 4. Adicional Compensación Anual Art. 4: Calculado sobre base de categoría (sin antigüedad). ¡Verificar base CCT!
        val adicionalCompAnualArt4Monto = salarioBasicoCategoria * COMP_ANUAL_ART4_PORCENTAJE

        // 5. Subtotal Bruto Remunerativo: Suma de todos los conceptos sujetos a descuentos de ley.
        val subtotalBrutoRemunerativo = baseConAntiguedad +
                adicionalPresentismoMonto +
                adicionalCompAnualArt4Monto +
                ADICIONAL_INCENTIVO_TRIVENTO_1 + // Asumiendo que son remunerativos
                ADICIONAL_INCENTIVO_TRIVENTO_2  // Asumiendo que son remunerativos

        // --- Cálculo de Descuentos ---
        // 6. Descuentos Específicos CCT (sobre bases particulares, ¡verificar CCT!)
        val descuentoSepelioMonto = SALARIO_BASE_OFICIAL * DESCUENTO_SEPELIO_PORCENTAJE // Sobre base oficial
        val aporteSolidarioMonto = salarioBasicoCategoria * APORTE_SOLIDARIO_PORCENTAJE   // Sobre base categoría

        // 7. Descuentos de Ley (sobre el Subtotal Bruto Remunerativo)
        val descuentoJubilacionMonto = subtotalBrutoRemunerativo * DESCUENTO_JUBILACION_PORCENTAJE
        val descuentoLey19032Monto = subtotalBrutoRemunerativo * DESCUENTO_LEY_19032_PORCENTAJE
        val descuentoObraSocialMonto = subtotalBrutoRemunerativo * DESCUENTO_OBRA_SOCIAL_PORCENTAJE

        // 8. Total Descuentos de Ley (sobre Remunerativo)
        val totalDescuentosRemunerativos = descuentoJubilacionMonto +
                descuentoLey19032Monto +
                descuentoObraSocialMonto +
                aporteSolidarioMonto // Incluir aporte solidario aquí si es sobre remunerativo bruto, si no, ajustar.

        // 9. Subtotal Neto Remunerativo: Lo que queda del bruto remunerativo tras descuentos de ley.
        val subtotalNetoRemunerativo = subtotalBrutoRemunerativo - totalDescuentosRemunerativos

        // --- Cálculo de Adicionales No Remunerativos y Horas Extras ---
        // 10. Calcular Valor Hora para Extras (basado en jornal de categoría)
        val jornal = salarioBasicoCategoria / DIAS_MES_JORNAL // Valor del día de trabajo
        val valorHoraOrdinaria = jornal / HORAS_JORNAL        // Valor de la hora normal

        // 11. Calcular Pago por Horas Extras (estos pagos suelen ser no remunerativos o tener tratamiento especial, ¡verificar CCT!)
        val pagoExtra50Monto = valorHoraOrdinaria * horasExtra50 * FACTOR_EXTRA_50
        val pagoExtra100Monto = valorHoraOrdinaria * horasExtra100 * FACTOR_EXTRA_100

        // --- Cálculo del Salario Neto Final ---
        // 12. Suma Final de Bolsillo: Neto Remunerativo + No Remunerativos + Extras - Otros descuentos.
        val salarioFinalNetoCalculado = subtotalNetoRemunerativo +  // Lo que quedó del remunerativo
                ADICIONAL_NO_REMUNERATIVO + // Suma adicional no remunerativo CCT
                REFRIGERIO +              // Suma refrigerio (asumiendo no remunerativo, ¡verificar!)
                pagoExtra50Monto +        // Suma pago extras 50%
                pagoExtra100Monto -       // Suma pago extras 100%
                descuentoSepelioMonto     // Resta descuento sepelio (si se aplica al final)

        // --- Actualizar el StateFlow con el Desglose Completo ---
        _salarioBreakdown.value = SalarioBreakdown(
            salarioBaseCalculado = baseConAntiguedad,
            adicionalPresentismo = adicionalPresentismoMonto,
            adicionalAntiguedad = adicionalAntiguedadMonto,
            adicionalCompAnualArt4 = adicionalCompAnualArt4Monto,
            adicionalIncentivoTrivento = ADICIONAL_INCENTIVO_TRIVENTO_1,
            adicionalIncentivoTrivento2 = ADICIONAL_INCENTIVO_TRIVENTO_2,
            subtotalBrutoRemunerativo = subtotalBrutoRemunerativo,
            descuentoSepelio = descuentoSepelioMonto,
            descuentoAporteSolidario = aporteSolidarioMonto,
            // Agrupa los descuentos de ley para mostrarlos juntos
            descuentoJubilacionLey = descuentoJubilacionMonto + descuentoLey19032Monto + descuentoObraSocialMonto,
            subtotalNetoRemunerativo = subtotalNetoRemunerativo,
            adicionalNoRemunerativo = ADICIONAL_NO_REMUNERATIVO,
            adicionalRefrigerio = REFRIGERIO,
            pagoExtra50 = pagoExtra50Monto,
            pagoExtra100 = pagoExtra100Monto,
            // Asegura que el salario final no sea negativo
            salarioFinalNeto = max(0.0, salarioFinalNetoCalculado)
        )
    }
}