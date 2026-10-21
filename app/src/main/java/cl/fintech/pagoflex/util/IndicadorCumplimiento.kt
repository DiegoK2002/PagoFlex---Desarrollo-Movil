
package cl.fintech.pagoflex.util

import cl.fintech.pagoflex.model.Compromiso
import cl.fintech.pagoflex.model.EstadoCompromiso
import cl.fintech.pagoflex.model.Pago
import java.time.LocalDate

data class IndicadorCumplimiento(
    val evaluados: Int,
    val pagadosATiempo: Int
) {
    val porcentaje: Double?
        get() = if (evaluados >= 3) {
            pagadosATiempo * 100.0 / evaluados
        } else {
            null
        }

    val categoria: String?
        get() = porcentaje?.let { valor ->
            when {
                valor >= 95.0 -> "Ejemplar"
                valor >= 80.0 -> "Al día"
                else -> "En camino"
            }
        }
}

fun calcularIndicadorCumplimiento(
    compromisos: List<Compromiso>,
    pagos: List<Pago>,
    hoy: LocalDate = LocalDate.now()
): IndicadorCumplimiento {
    val inicio = hoy.minusMonths(12)

    val evaluados = compromisos.filter { compromiso ->
        compromiso.fechaVencimiento.isBefore(hoy) &&
                !compromiso.fechaVencimiento.isBefore(inicio) &&
                compromiso.estado != EstadoCompromiso.ANULADO &&
                compromiso.estado != EstadoCompromiso.EN_REVISION
    }

    val pagadosATiempo = evaluados.count { compromiso ->
        compromiso.estado == EstadoCompromiso.PAGADO &&
                pagos.any { pago ->
                    pago.folioCompromiso == compromiso.folio &&
                            pago.idUsuario == compromiso.idUsuario &&
                            pago.aTiempo
                }
    }

    return IndicadorCumplimiento(
        evaluados = evaluados.size,
        pagadosATiempo = pagadosATiempo
    )
}
