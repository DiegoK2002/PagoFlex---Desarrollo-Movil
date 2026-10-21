package cl.fintech.pagoflex.model

import java.time.LocalDateTime

data class Pago(
    val numeroComprobante: String,
    val folioCompromiso: String,
    val idUsuario: Long,
    val codigoEmpresa: String,
    val concepto: String,
    val fechaHora: LocalDateTime,
    val montoPagado: Int,
    val canal: String,
    val aTiempo: Boolean,
    val medioPago: String = "Pago simulado"
)
