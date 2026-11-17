package cl.fintech.pagoflex.data.remote

import cl.fintech.pagoflex.model.Reporte
import java.time.LocalDateTime

class ReportesRepository(
    private val api: ReportesApi = ApiClient.reportesApi
) {

    suspend fun listarPorUsuario(idUsuario: Long): List<Reporte> {
        require(idUsuario > 0L)

        return api.listarPorUsuario(idUsuario)
            .filter { it.activo && it.idUsuario == idUsuario }
            .map { it.aModelo() }
            .ordenados()
    }

    suspend fun listarParaAgente(): List<Reporte> {
        return api.listarTodos()
            .filter { it.activo }
            .map { it.aModelo() }
            .ordenados()
    }

    suspend fun registrar(
        idUsuario: Long,
        folio: String,
        motivo: String,
        comentario: String
    ): Reporte {
        require(idUsuario > 0L)
        require(folio.isNotBlank() && folio.length <= 150)
        require(motivo.isNotBlank() && motivo.length <= 50)
        require(comentario.length <= 2000)

        val descripcion = comentario.trim()
            .ifBlank { "Sin comentario adicional" }

        return api.registrar(
            RegistrarReporteRequest(
                idUsuario = idUsuario,
                tipo = motivo.trim(),
                asunto = folio.trim(),
                descripcion = descripcion
            )
        ).aModelo()
    }

    suspend fun tomarReporte(idReporte: Long, idAgente: Long): Reporte {
        require(idReporte > 0L && idAgente > 0L)

        return api.actualizarSeguimiento(
            idReporte,
            SeguimientoReporteRequest(
                estado = "EN_PROCESO",
                idAgente = idAgente
            )
        ).aModelo()
    }

    suspend fun resolverReporte(
        idReporte: Long,
        respuesta: String
    ): Reporte {
        require(idReporte > 0L)
        require(respuesta.isNotBlank() && respuesta.length <= 2000)

        return api.actualizarSeguimiento(
            idReporte,
            SeguimientoReporteRequest(
                estado = "RESUELTO",
                respuesta = respuesta.trim()
            )
        ).aModelo()
    }

    private fun ReporteRemoto.aModelo(): Reporte {
        val estadoVisible = when (estado.trim().uppercase()) {
            "PENDIENTE" -> "Abierto"
            "EN_PROCESO" -> "En gestión"
            "RESUELTO" -> "Resuelto"
            else -> estado
        }

        return Reporte(
            idReporte = idReporte,
            folio = asunto,
            idUsuario = idUsuario,
            motivo = tipo,
            comentario = descripcion.takeUnless {
                it == "Sin comentario adicional"
            },
            estado = estadoVisible,
            idAgente = idAgente,
            respuesta = respuesta,
            fechaCreacion = LocalDateTime.parse(fechaCreacion)
        )
    }

    private fun List<Reporte>.ordenados(): List<Reporte> {
        return sortedWith(
            compareByDescending<Reporte> { it.fechaCreacion }
                .thenByDescending { it.idReporte }
        )
    }
}
