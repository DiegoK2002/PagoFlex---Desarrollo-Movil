package cl.fintech.pagoflex.model

import java.time.LocalDateTime

/**
 * Pago confirmado de un compromiso; equivale al comprobante.
 *
 * [montoPagado] incluye el recargo si el pago fue con atraso. [medioPago] es solo el nombre
 * del medio simulado: nunca se guardan numeros de tarjeta ni de cuenta.
 */
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
