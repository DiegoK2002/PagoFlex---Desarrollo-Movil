package cl.fintech.pagoflex.util

import cl.fintech.pagoflex.model.Pago
import java.time.LocalDate
import java.time.YearMonth

data class Reconocimiento(
    val titulo: String,
    val descripcion: String,
    val fechaObtenida: LocalDate?,
    val faltantes: Int,
    val meta: Int = 0,
    val unidad: String = "pagos a tiempo"
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
        ),
        Triple(
            10,
            "Diez pagos a tiempo",
            "Alcanzaste diez compromisos pagados dentro del plazo."
        ),
        Triple(
            20,
            "Veinte pagos a tiempo",
            "Alcanzaste veinte compromisos pagados dentro del plazo."
        )
    )

    val mesesPagados = pagosATiempo.groupBy {
        YearMonth.from(it.fechaHora)
    }.mapValues { (_, pagosDelMes) ->
        pagosDelMes.minOf { it.fechaHora.toLocalDate() }
    }

    var racha = 0
    var rachaUltimoMes = 0
    var mesAnterior: YearMonth? = null
    val fechasDeRacha = mutableMapOf<Int, LocalDate>()

    for (mes in mesesPagados.keys.sorted()) {
        racha = if (mesAnterior?.plusMonths(1) == mes) racha + 1 else 1
        if (racha == 2 || racha == 3) {
            fechasDeRacha.putIfAbsent(racha, mesesPagados.getValue(mes))
        }
        rachaUltimoMes = racha
        mesAnterior = mes
    }

    val mesActual = YearMonth.now()
    val rachaVigente = if (
        mesAnterior == mesActual || mesAnterior == mesActual.minusMonths(1)
    ) {
        rachaUltimoMes
    } else {
        0
    }

    val reconocimientosPorCantidad = metas.map { (cantidad, titulo, descripcion) ->
        Reconocimiento(
            titulo = titulo,
            descripcion = descripcion,
            fechaObtenida = pagosATiempo
                .getOrNull(cantidad - 1)
                ?.fechaHora
                ?.toLocalDate(),
            faltantes = (cantidad - pagosATiempo.size)
                .coerceAtLeast(0),
            meta = cantidad
        )
    }

    val metasDeConstancia = listOf(
        Triple(
            2,
            "Dos meses consecutivos",
            "Registraste pagos a tiempo en dos meses seguidos."
        ),
        Triple(
            3,
            "Tres meses consecutivos",
            "Registraste pagos a tiempo en tres meses seguidos."
        )
    )

    val reconocimientosDeConstancia = metasDeConstancia.map { (cantidad, titulo, descripcion) ->
        Reconocimiento(
            titulo = titulo,
            descripcion = descripcion,
            fechaObtenida = fechasDeRacha[cantidad],
            faltantes = if (fechasDeRacha.containsKey(cantidad)) {
                0
            } else {
                (cantidad - rachaVigente).coerceAtLeast(0)
            },
            meta = cantidad,
            unidad = "meses consecutivos"
        )
    }

    return reconocimientosPorCantidad + reconocimientosDeConstancia
}


fun cumpleMetaEjemplar(
    indicador: IndicadorCumplimiento
): Boolean {
    val porcentaje = indicador.porcentaje
    return porcentaje != null && porcentaje >= 95.0
}
