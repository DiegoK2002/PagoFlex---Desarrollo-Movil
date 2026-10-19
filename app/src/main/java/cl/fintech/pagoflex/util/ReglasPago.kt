package cl.fintech.pagoflex.util

import cl.fintech.pagoflex.model.Compromiso
import cl.fintech.pagoflex.model.EstadoCompromiso
import cl.fintech.pagoflex.model.MotivoNoPago

/**
 * Busca el compromiso mas antiguo de la misma persona y la misma empresa que vence antes que
 * [compromiso] y todavia esta sin pagar (pendiente o vencido). Los pagos deben hacerse en ese orden.
 */
fun compromisoAnterior(compromiso: Compromiso, todos: List<Compromiso>): Compromiso? =
    todos
        .filter {
            it.idUsuario == compromiso.idUsuario &&                       // misma persona
                it.codigoEmpresa == compromiso.codigoEmpresa &&           // misma empresa
                it.fechaVencimiento < compromiso.fechaVencimiento &&      // vence antes
                (it.estado == EstadoCompromiso.PENDIENTE || it.estado == EstadoCompromiso.VENCIDO)
        }
        .minByOrNull { it.fechaVencimiento }                              // el mas antiguo

/** Devuelve por que no se puede pagar el compromiso, o null si se puede pagar. */
fun motivoNoPagable(compromiso: Compromiso, anterior: Compromiso?): MotivoNoPago? = when {
    compromiso.estado == EstadoCompromiso.PAGADO -> MotivoNoPago.YA_PAGADO
    compromiso.estado == EstadoCompromiso.EN_REVISION -> MotivoNoPago.EN_REVISION
    compromiso.estado == EstadoCompromiso.ANULADO -> MotivoNoPago.ANULADO
    anterior != null -> MotivoNoPago.HAY_ANTERIOR
    else -> null
}
