package cl.fintech.pagoflex.util

import cl.fintech.pagoflex.model.Compromiso
import cl.fintech.pagoflex.model.EstadoCompromiso
import cl.fintech.pagoflex.model.MotivoNoPago

fun compromisoAnterior(
    compromiso: Compromiso,
    todos: List<Compromiso>
): Compromiso? =
    todos
        .filter {
            it.idUsuario == compromiso.idUsuario &&
                    it.codigoEmpresa == compromiso.codigoEmpresa &&
                    it.fechaVencimiento < compromiso.fechaVencimiento &&
                    (
                            it.estado == EstadoCompromiso.PENDIENTE ||
                                    it.estado == EstadoCompromiso.VENCIDO
                            )
        }
        .minByOrNull { it.fechaVencimiento }

fun motivoNoPagable(
    compromiso: Compromiso,
    anterior: Compromiso?
): MotivoNoPago? = when {
    compromiso.estado == EstadoCompromiso.PAGADO -> MotivoNoPago.YA_PAGADO
    compromiso.estado == EstadoCompromiso.EN_REVISION -> MotivoNoPago.EN_REVISION
    compromiso.estado == EstadoCompromiso.ANULADO -> MotivoNoPago.ANULADO
    anterior != null -> MotivoNoPago.HAY_ANTERIOR
    else -> null
}
