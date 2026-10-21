
package cl.fintech.pagoflex.util

import cl.fintech.pagoflex.model.Compromiso
import cl.fintech.pagoflex.model.EstadoCompromiso
import cl.fintech.pagoflex.model.Pago
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class IndicadorCumplimientoTest {

    private val hoy = LocalDate.of(2026, 10, 21)

    private fun compromiso(
        numero: Int,
        estado: EstadoCompromiso,
        fecha: LocalDate = hoy.minusDays(10)
    ) = Compromiso(
        folio = "CP-$numero",
        idUsuario = 1L,
        codigoEmpresa = "EMP",
        concepto = "Cuota",
        numeroCuota = null,
        totalCuotas = null,
        monto = 1000,
        fechaVencimiento = fecha,
        estado = estado
    )

    private fun pago(
        compromiso: Compromiso,
        aTiempo: Boolean
    ) = Pago(
        numeroComprobante = "CPR-${compromiso.folio}",
        folioCompromiso = compromiso.folio,
        idUsuario = compromiso.idUsuario,
        codigoEmpresa = compromiso.codigoEmpresa,
        concepto = compromiso.concepto,
        fechaHora = if (aTiempo) {
            compromiso.fechaVencimiento.atTime(10, 0)
        } else {
            compromiso.fechaVencimiento.plusDays(1).atTime(10, 0)
        },
        montoPagado = compromiso.monto,
        canal = "App",
        aTiempo = aTiempo
    )

    @Test
    fun menosDeTresEsHistorialInsuficiente() {
        val compromisos = listOf(
            compromiso(1, EstadoCompromiso.VENCIDO),
            compromiso(2, EstadoCompromiso.VENCIDO)
        )

        val resultado = calcularIndicadorCumplimiento(
            compromisos, emptyList(), hoy
        )

        assertEquals(2, resultado.evaluados)
        assertNull(resultado.porcentaje)
        assertNull(resultado.categoria)
    }

    @Test
    fun tresPagosATiempoDanCienPorCiento() {
        val compromisos = (1..3).map {
            compromiso(it, EstadoCompromiso.PAGADO)
        }

        val pagos = compromisos.map { pago(it, true) }

        val resultado = calcularIndicadorCumplimiento(
            compromisos, pagos, hoy
        )

        assertEquals(3, resultado.evaluados)
        assertEquals(3, resultado.pagadosATiempo)
        assertEquals(100.0, resultado.porcentaje!!, 0.001)
        assertEquals("Ejemplar", resultado.categoria)
    }

    @Test
    fun pagosAtrasadosNoCuentanComoATiempo() {
        val primero = compromiso(1, EstadoCompromiso.PAGADO)

        val compromisos = listOf(
            primero,
            compromiso(2, EstadoCompromiso.VENCIDO),
            compromiso(3, EstadoCompromiso.VENCIDO)
        )

        val resultado = calcularIndicadorCumplimiento(
            compromisos,
            listOf(pago(primero, false)),
            hoy
        )

        assertEquals(3, resultado.evaluados)
        assertEquals(0, resultado.pagadosATiempo)
        assertEquals(0.0, resultado.porcentaje!!, 0.001)
        assertEquals("En camino", resultado.categoria)
    }

    @Test
    fun excluyeAnuladosRevisionFuturosYAntiguos() {
        val primero = compromiso(1, EstadoCompromiso.PAGADO)

        val compromisos = listOf(
            primero,
            compromiso(2, EstadoCompromiso.VENCIDO),
            compromiso(3, EstadoCompromiso.VENCIDO),
            compromiso(4, EstadoCompromiso.ANULADO),
            compromiso(5, EstadoCompromiso.EN_REVISION),
            compromiso(
                6,
                EstadoCompromiso.PENDIENTE,
                hoy.plusDays(5)
            ),
            compromiso(
                7,
                EstadoCompromiso.VENCIDO,
                hoy.minusMonths(12).minusDays(1)
            )
        )

        val resultado = calcularIndicadorCumplimiento(
            compromisos,
            listOf(pago(primero, true)),
            hoy
        )

        assertEquals(3, resultado.evaluados)
        assertEquals(1, resultado.pagadosATiempo)
        assertEquals(33.333, resultado.porcentaje!!, 0.01)
        assertEquals("En camino", resultado.categoria)
    }

    @Test
    fun categoriasRespetanLosLimites() {
        val bajoOchenta = IndicadorCumplimiento(4, 3)
        val ochentaExacto = IndicadorCumplimiento(5, 4)
        val bajoNoventaYCinco = IndicadorCumplimiento(19, 18)
        val noventaYCincoExacto = IndicadorCumplimiento(20, 19)

        assertEquals("En camino", bajoOchenta.categoria)
        assertEquals("Al día", ochentaExacto.categoria)
        assertEquals("Al día", bajoNoventaYCinco.categoria)
        assertEquals("Ejemplar", noventaYCincoExacto.categoria)

        assertEquals(80.0, ochentaExacto.porcentaje!!, 0.001)
        assertEquals(95.0, noventaYCincoExacto.porcentaje!!, 0.001)
    }

}
