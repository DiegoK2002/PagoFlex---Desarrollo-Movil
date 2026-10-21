package cl.fintech.pagoflex.model

import java.time.LocalDateTime

data class Reporte(
    val idReporte: Long,
    val folio: String,
    val idUsuario: Long,
    val motivo: String,
    val comentario: String?,
    val estado: String,
    val idAgente: Long?,
    val respuesta: String?,
    val fechaCreacion: LocalDateTime
)
