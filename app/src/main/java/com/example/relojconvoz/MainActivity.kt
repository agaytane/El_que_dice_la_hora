package com.example.relojconvoz

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.relojconvoz.ui.theme.RelojConVozTheme
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import com.example.relojconvoz.ui.components.BotonReloj
import com.example.relojconvoz.ui.theme.FondoAnimado

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RelojConVozTheme {
                FondoAnimado {
                    UIPrincipal()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        detenerReproduccion()
    }
}

// 🎵 Variables globales para la reproducción
private var mediaPlayers: MutableList<MediaPlayer> = mutableListOf()
private var currentAudioIndex = 0

@Composable
fun UIPrincipal() {
    val context = LocalContext.current
    var horaActual by remember { mutableStateOf(obtenerHoraActual12Horas()) }

    LaunchedEffect(Unit) {
        while (true) {
            horaActual = obtenerHoraActual12Horas()
            delay(1000)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(containerColor = com.example.relojconvoz.ui.theme.AzulOscuro),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = horaActual,
                        style = com.example.relojconvoz.ui.theme.EstiloHora(),
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    Text(
                        text = SimpleDateFormat(
                            "EEEE, d 'de' MMMM",
                            Locale.getDefault()
                        ).format(Date()),
                        style = com.example.relojconvoz.ui.theme.EstiloFecha(),
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    BotonReloj(
                        texto = "Decir la hora",
                        onClick = { reproducirHoraEnVoz(context, horaActual) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

fun obtenerHoraActual12Horas(): String {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR)
    val minute = calendar.get(Calendar.MINUTE)
    val amPm = if (calendar.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM"


    val displayHour = if (hour == 0) 12 else hour

    // Formato HH:MM AM/PM
    return String.format("%d:%02d %s", displayHour, minute, amPm)
}


fun reproducirHoraEnVoz(context: Context, hora: String) {
    val partesHora = descomponerHoraParaAudio(hora)
    detenerReproduccion()

    try {
        mediaPlayers = partesHora.mapNotNull { parte ->
            val resourceId = obtenerResourceIdDeAudio(context, parte)
            if (resourceId != 0) {
                MediaPlayer.create(context, resourceId).apply {
                    setOnCompletionListener { reproducirSiguienteAudio() }
                }
            } else null
        }.toMutableList()

        currentAudioIndex = 0
        if (mediaPlayers.isNotEmpty()) mediaPlayers[currentAudioIndex].start()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun reproducirSiguienteAudio() {
    currentAudioIndex++
    if (currentAudioIndex < mediaPlayers.size) {
        try {
            mediaPlayers[currentAudioIndex].start()
        } catch (e: Exception) {
            e.printStackTrace()
            detenerReproduccion()
        }
    } else {
        detenerReproduccion()
    }
}

private fun detenerReproduccion() {
    mediaPlayers.forEach { player ->
        try {
            if (player.isPlaying) player.stop()
            player.release()
        } catch (_: Exception) {}
    }
    mediaPlayers.clear()
    currentAudioIndex = 0
}

fun descomponerHoraParaAudio(horaCompleta: String): List<String> {
    val partes = horaCompleta.split(" ")
    val horaMinuto = partes[0].split(":")
    val amPm = partes.getOrNull(1)?.uppercase(Locale.getDefault()) ?: "AM"

    val horaInt = horaMinuto[0].toIntOrNull() ?: 12
    val minutoInt = horaMinuto[1].toIntOrNull() ?: 0

    return when {
        horaInt == 12 && minutoInt == 0 && amPm == "AM" -> listOf("son_las", "doce", "am")
        horaInt == 12 && minutoInt == 0 && amPm == "PM" -> listOf("son_las", "doce", "pm")
        amPm == "AM" -> descomponerHoraAM(horaInt, minutoInt)
        else -> descomponerHoraPM(horaInt, minutoInt)
    }
}

private fun descomponerHoraAM(hora: Int, minuto: Int): List<String> {
    val partes = mutableListOf<String>()
    if (hora == 1) partes.addAll(listOf("son_la", "una"))
    else partes.addAll(listOf("son_las", convertirNumeroATexto(hora)))

    if (minuto > 0) partes.addAll(convertirMinutosATexto(minuto))

    partes.add("am")
    return partes
}

private fun descomponerHoraPM(hora: Int, minuto: Int): List<String> {
    val partes = mutableListOf<String>()
    if (hora == 1) partes.addAll(listOf("son_la", "una"))
    else partes.addAll(listOf("son_las", convertirNumeroATexto(hora)))

    if (minuto > 0) partes.addAll(convertirMinutosATexto(minuto))

    partes.add("pm")
    return partes
}

fun convertirMinutosATexto(minutos: Int): List<String> = when {
    minutos == 0 -> emptyList()
    minutos == 1 -> listOf("con_y", "un_minuto")
    minutos == 15 -> listOf("con_y", "quince", "minutos")
    minutos == 30 -> listOf("con_y", "treinta", "minutos")
    minutos in 2..14 -> listOf("con_y", convertirNumeroATexto(minutos), "minutos")
    minutos in 16..29 -> listOf("con_y", obtenerNombreAudioMinutoEspecial(minutos), "minutos")
    minutos in 31..59 -> listOf("con_y", obtenerNombreAudioDecena(minutos), "minutos")
    else -> listOf("con_y", minutos.toString())
}

fun convertirNumeroATexto(numero: Int): String = when (numero) {
    1 -> "una"
    2 -> "dos"
    3 -> "tres"
    4 -> "cuatro"
    5 -> "cinco"
    6 -> "seis"
    7 -> "siete"
    8 -> "ocho"
    9 -> "nueve"
    10 -> "diez"
    11 -> "once"
    12 -> "doce"
    13 -> "trece"
    14 -> "catorce"
    else -> numero.toString()
}

fun obtenerNombreAudioMinutoEspecial(minutos: Int): String = when (minutos) {
    16 -> "dieciseis"
    17 -> "diecisiete"
    18 -> "dieciocho"
    19 -> "diecinueve"
    20 -> "veinte"
    21 -> "veintiuno"
    22 -> "veintidos"
    23 -> "veintitres"
    24 -> "veinticuatro"
    25 -> "veinticinco"
    26 -> "veintiseis"
    27 -> "veintisiete"
    28 -> "veintiocho"
    29 -> "veintinueve"
    else -> minutos.toString()
}

fun obtenerNombreAudioDecena(minutos: Int): String = when (minutos) {
    31 -> "treinta_y_uno"
    32 -> "treinta_y_dos"
    33 -> "treinta_y_tres"
    34 -> "treinta_y_cuatro"
    35 -> "treinta_y_cinco"
    36 -> "treinta_y_seis"
    37 -> "treinta_y_siete"
    38 -> "treinta_y_ocho"
    39 -> "treinta_y_nueve"
    40 -> "cuarenta"
    41 -> "cuarenta_y_uno"
    42 -> "cuarenta_y_dos"
    43 -> "cuarenta_y_tres"
    44 -> "cuarenta_y_cuatro"
    45 -> "cuarenta_y_cinco"
    46 -> "cuarenta_y_seis"
    47 -> "cuarenta_y_siete"
    48 -> "cuarenta_y_ocho"
    49 -> "cuarenta_y_nueve"
    50 -> "cincuenta"
    51 -> "cincuenta_y_uno"
    52 -> "cincuenta_y_dos"
    53 -> "cincuenta_y_tres"
    54 -> "cincuenta_y_cuatro"
    55 -> "cincuenta_y_cinco"
    56 -> "cincuenta_y_seis"
    57 -> "cincuenta_y_siete"
    58 -> "cincuenta_y_ocho"
    59 -> "cincuenta_y_nueve"
    else -> minutos.toString()
}

fun obtenerResourceIdDeAudio(context: Context, parteAudio: String): Int = try {
    val resourceName = parteAudio.lowercase(Locale.getDefault())
    context.resources.getIdentifier(resourceName, "raw", context.packageName)
} catch (e: Exception) {
    println("No se encontró el audio: $parteAudio")
    0
}

@Preview(showBackground = true)
@Composable
fun PreviewUIPrincipal() {
    RelojConVozTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                shape = MaterialTheme.shapes.extraLarge,
                colors = CardDefaults.cardColors(containerColor = com.example.relojconvoz.ui.theme.AzulOscuro),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "12:34 PM", style = com.example.relojconvoz.ui.theme.EstiloHora())
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "miércoles, 10 de noviembre", style = com.example.relojconvoz.ui.theme.EstiloFecha())
                }
            }
        }
    }
}
