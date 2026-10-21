
package cl.fintech.pagoflex.util

import cl.fintech.pagoflex.model.Pago
import java.time.LocalDate

data class Reconocimiento(
    val titulo: String,
    val descripcion: String,
    val fechaObtenida: LocalDate?,
    val faltantes: Int
) {
    val obtenido: Boolean
        get() = fechaObtenida != null
}

fun calcularReconocimientosPorPagos(
    pagos: List<Pago>
): List<Reconocimiento> {
    val pagosATiempo = pagos
        .filter { it.aTiempo }
        .distinctBy { it.folioCompromiso }
        .sortedWith(
            compareBy<Pago> { it.fechaHora }
                .thenBy { it.numeroComprobante }
        )

    val metas = listOf(
        Triple(
            1,
            "Primer pago a tiempo",
            "Realizaste tu primer pago dentro del plazo."
        ),
        Triple(
            3,
            "Tres pagos a tiempo",
            "Completaste tres pagos dentro del plazo."
        ),
        Triple(
            5,
            "Cinco pagos a tiempo",
            "Completaste cinco pagos dentro del plazo."
        )
    )

    return metas.map { (cantidad, titulo, descripcion) ->
        Reconocimiento(
            titulo = titulo,
            descripcion = descripcion,
            fechaObtenida = pagosATiempo
                .getOrNull(cantidad - 1)
                ?.fechaHora
                ?.toLocalDate(),
            faltantes = (cantidad - pagosATiempo.size)
                .coerceAtLeast(0)
        )
    }
}


fun cumpleMetaEjemplar(
    indicador: IndicadorCumplimiento
): Boolean {
    val porcentaje = indicador.porcentaje
    return porcentaje != null && porcentaje >= 95.0
}
