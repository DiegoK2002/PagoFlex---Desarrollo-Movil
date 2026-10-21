
package cl.fintech.pagoflex.util

import cl.fintech.pagoflex.model.Pago
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

class ReconocimientosTest {

    private fun pago(
        numero: Int,
        dia: Int,
        aTiempo: Boolean = true
    ) = Pago(
        numeroComprobante = "COM-$numero-$dia",
        folioCompromiso = "CP-$numero",
        idUsuario = 1L,
        codigoEmpresa = "EMP",
        concepto = "Cuota",
        fechaHora = LocalDateTime.of(2026, 10, dia, 10, 0),
        montoPagado = 1000,
        canal = "App",
        aTiempo = aTiempo
    )

    @Test
    fun sinPagosTodosEstanPendientes() {
        val reconocimientos = calcularReconocimientosPorPagos(
            emptyList()
        )

        assertEquals(3, reconocimientos.size)
        assertTrue(reconocimientos.none { it.obtenido })
        assertEquals(
            listOf(1, 3, 5),
            reconocimientos.map { it.faltantes }
        )
    }

    @Test
    fun soloCuentanPagosATiempo() {
        val reconocimientos = calcularReconocimientosPorPagos(
            listOf(
                pago(1, 1, false),
                pago(2, 2, true)
            )
        )

        assertTrue(reconocimientos[0].obtenido)
        assertEquals(
            LocalDate.of(2026, 10, 2),
            reconocimientos[0].fechaObtenida
        )
        assertFalse(reconocimientos[1].obtenido)
        assertEquals(2, reconocimientos[1].faltantes)
    }

    @Test
    fun metasTienenFechasCorrectas() {
        val pagos = listOf(
            pago(5, 5),
            pago(1, 1),
            pago(3, 3),
            pago(2, 2),
            pago(4, 4)
        )

        val reconocimientos = calcularReconocimientosPorPagos(pagos)

        assertTrue(reconocimientos.all { it.obtenido })
        assertEquals(
            listOf(1, 3, 5),
            reconocimientos.map {
                it.fechaObtenida!!.dayOfMonth
            }
        )
        assertTrue(reconocimientos.all { it.faltantes == 0 })
    }

    @Test
    fun noCuentaDosVecesElMismoCompromiso() {
        val reconocimientos = calcularReconocimientosPorPagos(
            listOf(
                pago(1, 1),
                pago(1, 2),
                pago(2, 3)
            )
        )

        assertTrue(reconocimientos[0].obtenido)
        assertFalse(reconocimientos[1].obtenido)
        assertEquals(1, reconocimientos[1].faltantes)
        assertEquals(3, reconocimientos[2].faltantes)
    }

    @Test
    fun atrasoPosteriorNoQuitaReconocimientos() {
        val reconocimientos = calcularReconocimientosPorPagos(
            listOf(
                pago(1, 1),
                pago(2, 2),
                pago(3, 3),
                pago(4, 4, false)
            )
        )

        assertTrue(reconocimientos[0].obtenido)
        assertTrue(reconocimientos[1].obtenido)
        assertEquals(
            LocalDate.of(2026, 10, 3),
            reconocimientos[1].fechaObtenida
        )
        assertFalse(reconocimientos[2].obtenido)
        assertEquals(2, reconocimientos[2].faltantes)
    }

    @Test
    fun cumplimientoEjemplarRespetaMinimoYPorcentaje() {
        val historialInsuficiente = IndicadorCumplimiento(2, 2)
        val cienPorCiento = IndicadorCumplimiento(3, 3)
        val bajoNoventaYCinco = IndicadorCumplimiento(19, 18)
        val noventaYCincoExacto = IndicadorCumplimiento(20, 19)

        assertFalse(cumpleMetaEjemplar(historialInsuficiente))
        assertTrue(cumpleMetaEjemplar(cienPorCiento))
        assertFalse(cumpleMetaEjemplar(bajoNoventaYCinco))
        assertTrue(cumpleMetaEjemplar(noventaYCincoExacto))
    }
}
