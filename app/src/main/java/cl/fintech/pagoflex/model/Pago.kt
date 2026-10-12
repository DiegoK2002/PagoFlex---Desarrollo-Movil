package cl.fintech.pagoflex.model

import java.time.LocalDateTime

// Pago confirmado = comprobante. "canal": App, Portal de pagos o Agente conversacional.
// "medioPago": solo el NOMBRE del medio simulado.
data class Pago(
    val numeroComprobante: String,
    val folioCompromiso: String,
    val codigoUsuario: String,
    val codigoEmpresa: String,
    val concepto: String,
    val fechaHora: LocalDateTime,
    val montoPagado: Int,
    val canal: String,
    val aTiempo: Boolean,
    val medioPago: String = "Pago simulado"
)
