package cl.fintech.pagoflex.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Formatos del caso: montos como "$ 520.000" y fechas como DD-MM-AAAA.

fun formatearPesos(monto: Int): String {
    val miles = monto.toString().reversed().chunked(3).joinToString(".").reversed()
    return "\$ $miles"
}

fun formatearFecha(fecha: LocalDate): String =
    fecha.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))

fun formatearFechaHora(fechaHora: LocalDateTime): String =
    fechaHora.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))

// RUT parcialmente oculto: "15.834.207-3" -> "15.***.***-3"
fun ocultarRut(rut: String): String {
    val primero = rut.substringBefore('.')
    val digito = rut.substringAfterLast('-')
    return "${primero}.***.***-${digito}"
}
