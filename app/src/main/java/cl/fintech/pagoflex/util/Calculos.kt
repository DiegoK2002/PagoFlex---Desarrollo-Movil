package cl.fintech.pagoflex.util

import cl.fintech.pagoflex.model.Compromiso
import cl.fintech.pagoflex.model.EstadoCompromiso
import java.time.LocalDate
import java.time.temporal.ChronoUnit

fun calcularDiasDeAtraso(compromiso: Compromiso, hoy: LocalDate): Int {
    if (compromiso.estado != EstadoCompromiso.VENCIDO) return 0

    val dias = ChronoUnit.DAYS.between(
        compromiso.fechaVencimiento,
        hoy
    ).toInt()

    return if (dias > 0) dias else 0
}
