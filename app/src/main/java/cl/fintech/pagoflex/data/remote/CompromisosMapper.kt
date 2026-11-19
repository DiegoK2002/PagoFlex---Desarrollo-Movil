package cl.fintech.pagoflex.data.remote

import cl.fintech.pagoflex.model.Compromiso
import cl.fintech.pagoflex.model.EstadoCompromiso
import java.time.LocalDate

fun CompromisoRemoto.aCompromiso(): Compromiso {
    require(idCompromiso > 0L)
    require(idUsuario > 0L)
    require(idEmpresa > 0L)

    val vencimiento = LocalDate.parse(fechaVencimiento)
    val estadoServidor = EstadoCompromiso.valueOf(estado.trim().uppercase())
    val estadoVisible = if (
        estadoServidor == EstadoCompromiso.PENDIENTE &&
        vencimiento.isBefore(LocalDate.now())
    ) {
        EstadoCompromiso.VENCIDO
    } else {
        estadoServidor
    }

    return Compromiso(
        folio = "REM-$idCompromiso",
        idUsuario = idUsuario,
        codigoEmpresa = idEmpresa.toString(),
        concepto = nombre,
        numeroCuota = null,
        totalCuotas = null,
        monto = monto.intValueExact(),
        fechaVencimiento = vencimiento,
        estado = estadoVisible
    )
}
