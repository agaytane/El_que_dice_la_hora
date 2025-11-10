package com.example.relojconvoz.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Typography
import androidx.compose.material3.Shapes
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text

// 🎨 Colores personalizados
val AzulOscuro = Color(0xFF001F3F)
val AzulClaro = Color(0xFF0074D9)
val Blanco = Color(0xFFFFFFFF)
val GrisClaro = Color(0xFFDDDDDD)

// 🌙 Esquemas de color
private val DarkColorScheme = darkColorScheme(
    primary = AzulClaro,
    secondary = AzulOscuro,
    background = Color.Black,
    surface = AzulOscuro,
    onPrimary = Blanco,
    onSecondary = Blanco,
    onBackground = Blanco,
    onSurface = Blanco
)

private val LightColorScheme = lightColorScheme(
    primary = AzulOscuro,
    secondary = AzulClaro,
    background = Blanco,
    surface = GrisClaro,
    onPrimary = Blanco,
    onSecondary = Blanco,
    onBackground = Color.Black,
    onSurface = Color.Black
)

// Tipografía simple por defecto (ajusta si tienes fuentes personalizadas)
val AppTypography = Typography() // puedes personalizar esto

// Shapes por defecto (ajusta si quieres)
val AppShapes = Shapes()

// 🎭 Función de tema principal
@Composable
fun RelojConVozTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}

// Añade aquí estilos/estilos de texto reutilizables si los necesitas
// Ejemplo de estilos que usaste en MainActivity (si no existen aún)
@Composable
fun EstiloHora() = androidx.compose.material3.MaterialTheme.typography.displayLarge

@Composable
fun EstiloFecha() = androidx.compose.material3.MaterialTheme.typography.bodyLarge

