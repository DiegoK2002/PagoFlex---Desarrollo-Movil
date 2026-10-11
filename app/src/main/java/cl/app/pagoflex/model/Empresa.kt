package cl.app.pagoflex.model

// Empresa cliente de PagoFlex (la que cobra). "recargoAtraso" en pesos (RN-03).
data class Empresa(
    val codigo: String,
    val nombreFantasia: String,
    val razonSocial: String,
    val rubro: String,
    val recargoAtraso: Int
)
