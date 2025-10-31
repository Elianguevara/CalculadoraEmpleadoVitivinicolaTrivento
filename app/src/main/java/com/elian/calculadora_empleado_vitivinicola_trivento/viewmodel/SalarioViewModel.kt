package com.elian.calculadora_empleado_vitivinicola_trivento.viewmodel

import androidx.lifecycle.ViewModel
import com.elian.calculadora_empleado_vitivinicola_trivento.model.Categoria
import com.elian.calculadora_empleado_vitivinicola_trivento.model.escalasAntiguedad
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.math.max

/**
 * Data class para almacenar el desglose detallado del cálculo del salario.
 * Incluye todos los conceptos remunerativos, no remunerativos y descuentos
 * según el CCT Nº 154/91 - Obreros de Viña (Octubre 2025).
 */
data class SalarioBreakdown(
    val salarioBaseCalculado: Double = 0.0,
    val adicionalPresentismo: Double = 0.0,
    val adicionalAntiguedad: Double = 0.0,
    val adicionalCompAnualArt4: Double = 0.0,
    val adicionalIncentivoTrivento: Double = 0.0,
    val adicionalIncentivoTrivento2: Double = 0.0,
    val subtotalBrutoRemunerativo: Double = 0.0,
    val descuentoSepelio: Double = 0.0,
    val descuentoAporteSolidario: Double = 0.0,
    val descuentoJubilacionLey: Double = 0.0,
    val subtotalNetoRemunerativo: Double = 0.0,
    val adicionalNoRemunerativo: Double = 0.0,
    val adicionalRefrigerio: Double = 0.0,
    val pagoExtra50: Double = 0.0,
    val pagoExtra100: Double = 0.0,
    val salarioFinalNeto: Double = 0.0,
    val pagoExtra50Neto: Double = 0.0,
    val pagoExtra100Neto: Double = 0.0
)

@HiltViewModel
class SalarioViewModel @Inject constructor() : ViewModel() {

    companion object {
        // --- Datos base del Convenio 154/91 (Octubre 2025) ---
        const val SALARIO_BASE_OFICIAL = 401009.0       // Obrero Común sin antigüedad
        const val ADICIONAL_NO_REMUNERATIVO = 172776.0  // Suma mensual no remunerativa
        const val REFRIGERIO = 137604.0                 // Valor mensual por refrigerio
        const val ADICIONAL_INCENTIVO_TRIVENTO_1 = 30000.0
        const val ADICIONAL_INCENTIVO_TRIVENTO_2 = 35000.0

        // --- Porcentajes oficiales ---
        const val PRESENTISMO_PORCENTAJE = 0.05
        const val COMP_ANUAL_ART4_PORCENTAJE = 0.0532
        const val APORTE_SOLIDARIO_PORCENTAJE = 0.015   // 1,5% CCT
        // Descuentos de ley estándar (total 17%)
        const val DESCUENTO_JUBILACION_PORCENTAJE = 0.11
        const val DESCUENTO_LEY_19032_PORCENTAJE = 0.03
        const val DESCUENTO_OBRA_SOCIAL_PORCENTAJE = 0.03
        const val TOTAL_DESCUENTOS_LEY_PORCENTAJE =
            DESCUENTO_JUBILACION_PORCENTAJE + DESCUENTO_LEY_19032_PORCENTAJE + DESCUENTO_OBRA_SOCIAL_PORCENTAJE

        // --- Parámetros de jornada ---
        const val DIAS_MES_JORNAL = 25.0
        const val HORAS_JORNAL = 8.0
        const val FACTOR_EXTRA_50 = 1.5
        const val FACTOR_EXTRA_100 = 2.0
    }

    private val _salarioBreakdown = MutableStateFlow(SalarioBreakdown())
    val salarioBreakdown: StateFlow<SalarioBreakdown> = _salarioBreakdown.asStateFlow()

    /**
     * Calcula el salario completo según el CCT actualizado (Octubre 2025).
     */
    fun calcularSalario(
        categoria: Categoria,
        antiguedadIndex: Int,
        horasExtra100: Int,
        horasExtra50: Int
    ) {
        // --- Factor de antigüedad según escala oficial ---
        val factorAntiguedad = escalasAntiguedad.getOrNull(antiguedadIndex) ?: 1.0
        val salarioBasicoCategoria = SALARIO_BASE_OFICIAL * categoria.factor
        val baseConAntiguedad = salarioBasicoCategoria * factorAntiguedad
        val adicionalAntiguedadMonto = baseConAntiguedad - salarioBasicoCategoria

        // --- Adicionales remunerativos ---
        val adicionalPresentismoMonto = SALARIO_BASE_OFICIAL * PRESENTISMO_PORCENTAJE
        val adicionalCompAnualArt4Monto = salarioBasicoCategoria * COMP_ANUAL_ART4_PORCENTAJE

        // --- Cálculo de horas extras ---
        val jornal = salarioBasicoCategoria / DIAS_MES_JORNAL
        val valorHoraOrdinaria = jornal / HORAS_JORNAL
        val pagoExtra50Monto = valorHoraOrdinaria * horasExtra50 * FACTOR_EXTRA_50
        val pagoExtra100Monto = valorHoraOrdinaria * horasExtra100 * FACTOR_EXTRA_100
        val pagoExtra50NetoMonto = pagoExtra50Monto * (1 - TOTAL_DESCUENTOS_LEY_PORCENTAJE)
        val pagoExtra100NetoMonto = pagoExtra100Monto * (1 - TOTAL_DESCUENTOS_LEY_PORCENTAJE)

        // --- Subtotal bruto remunerativo ---
        val subtotalBrutoRemunerativo = baseConAntiguedad +
                adicionalPresentismoMonto +
                adicionalCompAnualArt4Monto +
                ADICIONAL_INCENTIVO_TRIVENTO_1 +
                ADICIONAL_INCENTIVO_TRIVENTO_2 +
                pagoExtra50Monto +
                pagoExtra100Monto

        // --- Descuentos ---
        val descuentoSepelioMonto = (SALARIO_BASE_OFICIAL / DIAS_MES_JORNAL) * 0.4 // CCT 2025: 40% de un jornal
        val aporteSolidarioMonto = salarioBasicoCategoria * APORTE_SOLIDARIO_PORCENTAJE

        val descuentoJubilacionMonto = subtotalBrutoRemunerativo * DESCUENTO_JUBILACION_PORCENTAJE
        val descuentoLey19032Monto = subtotalBrutoRemunerativo * DESCUENTO_LEY_19032_PORCENTAJE
        val descuentoObraSocialMonto = subtotalBrutoRemunerativo * DESCUENTO_OBRA_SOCIAL_PORCENTAJE

        val totalDescuentosRemunerativos = descuentoJubilacionMonto +
                descuentoLey19032Monto +
                descuentoObraSocialMonto +
                aporteSolidarioMonto

        val subtotalNetoRemunerativo = subtotalBrutoRemunerativo - totalDescuentosRemunerativos

        // --- Cálculo del salario final de bolsillo ---
        val salarioFinalNetoCalculado = subtotalNetoRemunerativo +
                ADICIONAL_NO_REMUNERATIVO +
                REFRIGERIO -
                descuentoSepelioMonto

        // --- Actualizar flujo ---
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
            descuentoJubilacionLey = descuentoJubilacionMonto + descuentoLey19032Monto + descuentoObraSocialMonto,
            subtotalNetoRemunerativo = subtotalNetoRemunerativo,
            adicionalNoRemunerativo = ADICIONAL_NO_REMUNERATIVO,
            adicionalRefrigerio = REFRIGERIO,
            pagoExtra50 = pagoExtra50Monto,
            pagoExtra100 = pagoExtra100Monto,
            salarioFinalNeto = max(0.0, salarioFinalNetoCalculado),
            pagoExtra50Neto = pagoExtra50NetoMonto,
            pagoExtra100Neto = pagoExtra100NetoMonto
        )
    }
}
