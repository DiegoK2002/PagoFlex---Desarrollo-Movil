package cl.fintech.pagoflex.model

// Empresa cliente de PagoFlex (la que cobra). "recargoAtraso" en pesos.
data class Empresa(
    val codigo: String,
    val nombreFantasia: String,
    val razonSocial: String,
    val rubro: String,
    val recargoAtraso: Int
)
