package cl.fintech.pagoflex.util

import cl.fintech.pagoflex.model.Compromiso
import cl.fintech.pagoflex.model.EstadoCompromiso
import cl.fintech.pagoflex.model.MotivoNoPago

// =====================================================================
//  ReglasPago.kt  —  CUÁNDO SE PUEDE PAGAR UN COMPROMISO
//  ---------------------------------------------------------------------
//  Funciones simples para poder reutilizarlas
//  y revisarlas fácilmente.
//    se paga primero el vencimiento más antiguo de cada empresa.
//    un compromiso En revisión no se paga.
//    Anulado y ya Pagado tampoco se pagan.
// =====================================================================

/** El compromiso más antiguo, de la misma persona y empresa, que aún está sin pagar (RN-04). */
fun compromisoAnterior(compromiso: Compromiso, todos: List<Compromiso>): Compromiso? =
    todos
        .filter {
            it.codigoUsuario == compromiso.codigoUsuario &&
                it.codigoEmpresa == compromiso.codigoEmpresa &&
                it.fechaVencimiento < compromiso.fechaVencimiento &&
                (it.estado == EstadoCompromiso.PENDIENTE || it.estado == EstadoCompromiso.VENCIDO)
        }
        .minByOrNull { it.fechaVencimiento }

/** null = se puede pagar. Si no, devuelve el motivo para explicárselo a la persona. */
fun motivoNoPagable(compromiso: Compromiso, anterior: Compromiso?): MotivoNoPago? = when {
    compromiso.estado == EstadoCompromiso.PAGADO -> MotivoNoPago.YA_PAGADO
    compromiso.estado == EstadoCompromiso.EN_REVISION -> MotivoNoPago.EN_REVISION
    compromiso.estado == EstadoCompromiso.ANULADO -> MotivoNoPago.ANULADO
    anterior != null -> MotivoNoPago.HAY_ANTERIOR
    else -> null
}
