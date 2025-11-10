package com.example.relojconvoz.ui.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// 🔧 Convertidor manual de Color a AnimationVector4D (para Compose viejas)
private val ColorToVectorConverter: TwoWayConverter<Color, AnimationVector4D> =
    TwoWayConverter(
        convertToVector = { color ->
            AnimationVector4D(
                color.red,
                color.green,
                color.blue,
                color.alpha
            )
        },
        convertFromVector = { vector ->
            Color(vector.v1, vector.v2, vector.v3, vector.v4)
        }
    )

@Composable
fun FondoAnimado(content: @Composable () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "fondo_animado")

    val color1 by infiniteTransition.animateValue(
        initialValue = Color(0xFF001F3F),
        targetValue = Color(0xFF0074D9),
        typeConverter = ColorToVectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "color1"
    )

    val color2 by infiniteTransition.animateValue(
        initialValue = Color(0xFF0074D9),
        targetValue = Color(0xFF001F3F),
        typeConverter = ColorToVectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "color2"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(listOf(color1, color2))
            )
    ) {
        content()
    }
}
