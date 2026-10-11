package cl.app.pagoflex.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter

// Formatos del caso (RN-17): montos como "$ 520.000" y fechas como DD-MM-AAAA.

fun formatearPesos(monto: Int): String {
    val miles = monto.toString().reversed().chunked(3).joinToString(".").reversed()
    return "\$ $miles"
}

fun formatearFecha(fecha: LocalDate): String =
    fecha.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
