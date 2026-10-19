package cl.fintech.pagoflex.model

/** Empresa que cobra un compromiso. [recargoAtraso] es el monto en pesos que suma si el pago se atrasa. */
data class Empresa(
    val codigo: String,
    val nombreFantasia: String,
    val razonSocial: String,
    val rubro: String,
    val recargoAtraso: Int
)
