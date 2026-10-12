package cl.fintech.pagoflex.util

import cl.fintech.pagoflex.model.Compromiso
import cl.fintech.pagoflex.model.EstadoCompromiso
import java.time.LocalDate
import java.time.temporal.ChronoUnit

// =====================================================================
//  Calculos.kt  —  DATOS QUE SE CALCULAN Y NO SE GUARDAN
//  ---------------------------------------------------------------------
//  Días de atraso = hoy - fecha de vencimiento, solo si está Vencido.
//  Total a pagar  = monto + recargo (el recargo solo si hay atraso.
//  Ejemplo: CP-2026-0106 -> 6 días, $310.000 + $8.500 = $318.500.
// =====================================================================


//IMPORTANTE: SE CAMBIÓ EL MINIMO SDK DE 24 A 26 PARA QUE FUNCIONE

fun calcularDiasDeAtraso(compromiso: Compromiso, hoy: LocalDate): Int {
    if (compromiso.estado != EstadoCompromiso.VENCIDO) return 0
    val dias = ChronoUnit.DAYS.between(compromiso.fechaVencimiento, hoy).toInt()
    return if (dias > 0) dias else 0
}
