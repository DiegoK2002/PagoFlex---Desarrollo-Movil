package cl.app.pagoflex.util

import cl.app.pagoflex.model.Compromiso
import cl.app.pagoflex.model.EstadoCompromiso
import java.time.LocalDate
import java.time.temporal.ChronoUnit

// =====================================================================
//  Calculos.kt  —  DATOS QUE SE CALCULAN Y NO SE GUARDAN (MER del docente)
//  ---------------------------------------------------------------------
//  Días de atraso = hoy - fecha de vencimiento (RN-02), solo si está Vencido.
//  Total a pagar  = monto + recargo (el recargo solo si hay atraso, RN-03).
//  Ejemplo del Anexo 9: CP-2026-0106 -> 6 días, $310.000 + $8.500 = $318.500.
// =====================================================================

fun calcularDiasDeAtraso(compromiso: Compromiso, hoy: LocalDate): Int {
    if (compromiso.estado != EstadoCompromiso.VENCIDO) return 0
    val dias = ChronoUnit.DAYS.between(compromiso.fechaVencimiento, hoy).toInt()
    return if (dias > 0) dias else 0
}
