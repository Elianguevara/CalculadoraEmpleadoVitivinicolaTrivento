# 🍇 Calculadora Salarial - Empleado Vitivinícola (Trivento)

![Android](https://img.shields.io/badge/Android-35-3DDC84?style=for-the-badge&logo=android)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9-7F52FF?style=for-the-badge&logo=kotlin)
![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-Material3-4285F4?style=for-the-badge&logo=jetpackcompose)
![Hilt](https://img.shields.io/badge/DI-Hilt-orange?style=for-the-badge&logo=google)

## 📱 Descripción

Esta aplicación Android es una herramienta especializada diseñada para simular y calcular el salario de los trabajadores vitivinícolas bajo el **Convenio Colectivo de Trabajo (CCT) N° 154/91** (Obreros de Viña), actualizada con las escalas salariales de **Octubre 2025**.

La app permite realizar cálculos precisos de haberes netos y brutos, contemplando antigüedad, categorías específicas, horas extras y los incentivos particulares de la empresa **Trivento**.

## 🚀 Características

### 🧮 Cálculo de Conceptos Remunerativos
* **Salario Básico:** Ajustado por categoría (Obrero Común, Especializado, Tractorista, Mecánico, etc.).
* **Antigüedad:** Cálculo automático basado en escalas porcentuales por años de servicio.
* **Presentismo:** Adicional del 5% sobre el básico.
* **Horas Extras:** Cálculo automático de horas al 50% y al 100%.
* **Incentivos Trivento:** Inclusión de bonos específicos de la empresa.

### 📉 Deducciones y Retenciones
* **Aportes de Ley:** Jubilación (11%), Ley 19.032 (3%), Obra Social (3%).
* **Sindicales:** Aporte solidario CCT (1.5%) y seguro de sepelio.

### 💵 Conceptos No Remunerativos
* **Ítems Adicionales:** Sumas fijas no remunerativas y viáticos por refrigerio.

### 📊 Desglose Detallado
* Visualización clara del **Subtotal Bruto**, **Total de Descuentos** y **Salario Neto de Bolsillo**.
* Interfaz intuitiva construida con **Material Design 3**.

## 🛠️ Stack Tecnológico

El proyecto sigue una arquitectura **MVVM (Model-View-ViewModel)** y utiliza las últimas tecnologías de desarrollo nativo Android:

* **Lenguaje:** Kotlin
* **UI Toolkit:** Jetpack Compose (Material 3)
* **Inyección de Dependencias:** Dagger Hilt
* **Gestión de Estado:** ViewModel + StateFlow
* **Asincronismo:** Coroutines
* **Build System:** Gradle KTS (Kotlin DSL)

## 📸 Capturas de Pantalla

| Ingreso de Datos | Resultado del Cálculo |
|:----------------:|:---------------------:|
| ![Input Screen](https://via.placeholder.com/300x600?text=Pantalla+Ingreso) | ![Result Screen](https://via.placeholder.com/300x600?text=Desglose+Salario) |
| *Selección de categoría y horas* | *Detalle de haberes y descuentos* |

*(Reemplaza estos enlaces con capturas reales de tu emulador)*

## 🧩 Lógica de Negocio

La aplicación implementa la lógica financiera basada en:
1.  **Escalas de Categorías:** Factores multiplicadores desde 1.0 (Obrero Común) hasta 1.25 (Mecánico).
2.  **Cálculo de Jornada:** Basado en un divisor de 25 días y 8 horas diarias para obtener el valor hora.
3.  **Matemática de Bolsillo:** Deducción precisa de impuestos sobre los montos remunerativos y suma directa de los no remunerativos.

## ⚙️ Configuración e Instalación

### Prerrequisitos
* Android Studio Ladybug o superior.
* JDK 17 (Java 11 mínimo requerido por el proyecto).
* Dispositivo o Emulador con Android 7.0 (API 24) o superior.

### Pasos
1.  Clonar el repositorio:
    ```bash
    git clone [https://github.com/elianguevara/CalculadoraEmpleadoVitivinicolaTrivento.git](https://github.com/elianguevara/CalculadoraEmpleadoVitivinicolaTrivento.git)
    ```
2.  Abrir en Android Studio.
3.  Sincronizar el proyecto con Gradle (`Sync Project with Gradle Files`).
4.  Ejecutar la app (`Shift + F10`).

## 📂 Estructura del Proyecto

```bash
com.elian.calculadora_empleado_vitivinicola_trivento
├── di/              # Módulos de Hilt (si aplica en el futuro)
├── model/           # Data Classes (Categoria, SalaryBreakdown)
├── ui/              # Componentes Jetpack Compose (SalarioScreen)
├── viewmodel/       # Lógica de presentación (SalarioViewModel)
├── MyApplication.kt # Hilt Entry Point
└── MainActivity.kt  # Activity principal
