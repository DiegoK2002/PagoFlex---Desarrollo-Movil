package cl.fintech.pagoflex.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/** Da formato de pesos chilenos con separador de miles: 520000 -> "$ 520.000". */
fun formatearPesos(monto: Int): String {
    // Se agrupan los digitos de a tres desde la derecha y se unen con puntos
    val miles = monto.toString().reversed().chunked(3).joinToString(".").reversed()
    return "\$ $miles"
}

/** Da formato dia-mes-ano: 2026-10-10 -> "10-10-2026". */
fun formatearFecha(fecha: LocalDate): String =
    fecha.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))

/** Da formato dia-mes-ano con hora y minutos: "10-10-2026 14:35". */
fun formatearFechaHora(fechaHora: LocalDateTime): String =
    fechaHora.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))

/** Oculta la parte central de un RUT con formato "15.834.207-3" y devuelve "15.***.***-3". */
fun ocultarRut(rut: String): String {
    val primero = rut.substringBefore('.')      // primer grupo de digitos
    val digito = rut.substringAfterLast('-')    // digito verificador
    return "${primero}.***.***-${digito}"
}
