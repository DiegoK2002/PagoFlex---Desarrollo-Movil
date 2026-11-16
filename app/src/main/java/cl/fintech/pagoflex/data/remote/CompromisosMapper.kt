package cl.fintech.pagoflex.data.remote

import cl.fintech.pagoflex.model.Compromiso
import cl.fintech.pagoflex.model.EstadoCompromiso
import java.time.LocalDate

fun CompromisoRemoto.aCompromiso(): Compromiso {
    require(idCompromiso > 0L)
    require(idUsuario > 0L)
    require(idEmpresa > 0L)

    return Compromiso(
        folio = "REM-$idCompromiso",
        idUsuario = idUsuario,
        codigoEmpresa = idEmpresa.toString(),
        concepto = nombre,
        numeroCuota = null,
        totalCuotas = null,
        monto = monto.intValueExact(),
        fechaVencimiento = LocalDate.parse(fechaVencimiento),
        estado = EstadoCompromiso.valueOf(
            estado.trim().uppercase()
        )
    )
}
