package cl.app.pagoflex.model

import java.time.LocalDateTime

// Pago confirmado = comprobante (RN-05). "canal": App, Portal de pagos o Agente conversacional (RN-07).
// "medioPago": solo el NOMBRE del medio simulado; nunca números de tarjeta ni cuentas (RN-19).
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
