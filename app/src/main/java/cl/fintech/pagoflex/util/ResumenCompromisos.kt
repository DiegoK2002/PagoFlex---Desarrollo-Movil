package cl.fintech.pagoflex.util

import cl.fintech.pagoflex.model.Compromiso
import cl.fintech.pagoflex.model.EstadoCompromiso
import java.time.LocalDate
import java.time.YearMonth

data class ResumenCompromisos(
    val totalPorPagarMes: Int,
    val proximoVencimiento: LocalDate?,
    val cantidadVencidos: Int
)

fun calcularResumenCompromisos(
    compromisos: List<Compromiso>,
    importeDe: (Compromiso) -> Int,
    hoy: LocalDate = LocalDate.now()
): ResumenCompromisos {
    val activos = compromisos.filter {
        it.estado == EstadoCompromiso.PENDIENTE ||
                it.estado == EstadoCompromiso.VENCIDO
    }

    val mesActual = YearMonth.from(hoy)

    val totalMes = activos
        .filter {
            YearMonth.from(it.fechaVencimiento) == mesActual
        }
        .sumOf { importeDe(it) }

    val proximo = activos
        .filter {
            !it.fechaVencimiento.isBefore(hoy)
        }
        .minOfOrNull { it.fechaVencimiento }

    val vencidos = activos.count {
        it.fechaVencimiento.isBefore(hoy)
    }

    return ResumenCompromisos(
        totalPorPagarMes = totalMes,
        proximoVencimiento = proximo,
        cantidadVencidos = vencidos
    )
}
