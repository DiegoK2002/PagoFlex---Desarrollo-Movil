package cl.fintech.pagoflex.data.remote

import java.math.BigDecimal
import java.time.LocalDate

class PagosRepository(
    private val api: PagosApi = ApiClient.pagosApi
) {

    suspend fun listarPorUsuario(
        idUsuario: Long
    ): List<PagoRemoto> {

        require(idUsuario > 0L)

        return api.listarPorUsuario(idUsuario)
            .filter {
                it.idUsuario == idUsuario && it.activo
            }
            .sortedWith(
                compareByDescending<PagoRemoto> {
                    it.fechaPago
                }.thenByDescending {
                    it.idPago
                }
            )
    }

    suspend fun buscarComprobante(
        idUsuario: Long,
        idCompromiso: Long
    ): PagoRemoto? {
        require(idUsuario > 0L)
        require(idCompromiso > 0L)

        return api.listarPorCompromiso(idCompromiso)
            .filter {
                it.activo &&
                it.idUsuario == idUsuario &&
                it.idCompromiso == idCompromiso
            }
            .maxByOrNull { it.idPago }
    }

    suspend fun registrarPago(
        idUsuario: Long,
        idCompromiso: Long,
        monto: BigDecimal,
        metodoPago: String
    ): PagoRemoto {

        require(idUsuario > 0L)
        require(idCompromiso > 0L)
        require(monto > BigDecimal.ZERO)
        require(metodoPago.isNotBlank())

        return api.registrarPago(
            RegistrarPagoRequest(
                idUsuario = idUsuario,
                idCompromiso = idCompromiso,
                monto = monto,
                fechaPago = LocalDate.now().toString(),
                metodoPago = metodoPago.trim()
            )
        )
    }
}
