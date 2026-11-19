package cl.fintech.pagoflex.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun formatearPesos(monto: Int): String {
    val miles = monto.toString().reversed().chunked(3).joinToString(".").reversed()
    return "\$ $miles"
}

fun formatearFecha(fecha: LocalDate): String =
    fecha.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))

fun formatearFechaHora(fechaHora: LocalDateTime): String =
    fechaHora.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))


fun ocultarRut(rut: String): String {
    val numero = rut.substringBefore('-').filter { it.isDigit() }
    val digito = rut.substringAfterLast('-').trim().uppercase()

    if (numero.length !in 7..8 || digito.length != 1) {
        return "RUT no disponible"
    }

    val primero = numero.dropLast(6)
    return "$primero.***.***-$digito"
}
