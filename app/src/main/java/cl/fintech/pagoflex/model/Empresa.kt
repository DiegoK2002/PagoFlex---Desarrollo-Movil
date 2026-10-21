package cl.fintech.pagoflex.model

data class Empresa(
    val codigo: String,
    val nombreFantasia: String,
    val razonSocial: String,
    val rubro: String,
    val recargoAtraso: Int
)
