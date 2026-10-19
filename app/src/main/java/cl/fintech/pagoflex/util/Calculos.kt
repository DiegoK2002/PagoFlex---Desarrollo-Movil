package cl.fintech.pagoflex.util

import cl.fintech.pagoflex.model.Compromiso
import cl.fintech.pagoflex.model.EstadoCompromiso
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/** Fecha que la app toma como "hoy". Coincide con la fecha de los datos de ejemplo. */
val FECHA_REFERENCIA: LocalDate = LocalDate.of(2026, 10, 6)

/** Dias transcurridos desde el vencimiento. Solo cuenta si el compromiso esta vencido; si no, devuelve 0. */
fun calcularDiasDeAtraso(compromiso: Compromiso, hoy: LocalDate): Int {
    if (compromiso.estado != EstadoCompromiso.VENCIDO) return 0
    val dias = ChronoUnit.DAYS.between(compromiso.fechaVencimiento, hoy).toInt()
    return if (dias > 0) dias else 0
}
