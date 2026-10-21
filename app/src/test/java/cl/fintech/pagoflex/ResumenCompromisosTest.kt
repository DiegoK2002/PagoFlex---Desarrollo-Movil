package cl.fintech.pagoflex.util

import cl.fintech.pagoflex.model.Compromiso
import cl.fintech.pagoflex.model.EstadoCompromiso
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class ResumenCompromisosTest {

    private val hoy = LocalDate.of(2026, 10, 21)

    private fun compromiso(
        folio: String,
        monto: Int,
        fecha: LocalDate,
        estado: EstadoCompromiso = EstadoCompromiso.PENDIENTE
    ) = Compromiso(
        folio = folio,
        idUsuario = 1L,
        codigoEmpresa = "EMP-01",
        concepto = "Compromiso de prueba",
        numeroCuota = null,
        totalCuotas = null,
        monto = monto,
        fechaVencimiento = fecha,
        estado = estado
    )

    @Test
    fun listaVaciaGeneraResumenEnCero() {
        val resumen = calcularResumenCompromisos(
            compromisos = emptyList(),
            importeDe = { it.monto },
            hoy = hoy
        )

        assertEquals(0, resumen.totalPorPagarMes)
        assertEquals(0, resumen.cantidadVencidos)
        assertNull(resumen.proximoVencimiento)
    }

    @Test
    fun totalMensualExcluyeEstadosNoPagables() {
        val compromisos = listOf(
            compromiso(
                "P1", 10000, hoy.plusDays(4)
            ),
            compromiso(
                "V1", 20000, hoy.minusDays(4),
                EstadoCompromiso.VENCIDO
            ),
            compromiso(
                "P2", 30000, hoy,
                EstadoCompromiso.PAGADO
            ),
            compromiso(
                "R1", 40000, hoy,
                EstadoCompromiso.EN_REVISION
            ),
            compromiso(
                "A1", 50000, hoy,
                EstadoCompromiso.ANULADO
            ),
            compromiso(
                "N1", 60000, hoy.plusMonths(1)
            )
        )

        val resumen = calcularResumenCompromisos(
            compromisos = compromisos,
            importeDe = { it.monto + 500 },
            hoy = hoy
        )

        assertEquals(31000, resumen.totalPorPagarMes)
    }

    @Test
    fun proximoVencimientoPuedeSerHoy() {
        val compromisos = listOf(
            compromiso(
                "V1", 5000, hoy.minusDays(1),
                EstadoCompromiso.VENCIDO
            ),
            compromiso(
                "F1", 5000, hoy.plusDays(3)
            ),
            compromiso(
                "H1", 5000, hoy
            ),
            compromiso(
                "P1", 5000, hoy.minusDays(2),
                EstadoCompromiso.PAGADO
            )
        )

        val resumen = calcularResumenCompromisos(
            compromisos = compromisos,
            importeDe = { it.monto },
            hoy = hoy
        )

        assertEquals(hoy, resumen.proximoVencimiento)
    }

    @Test
    fun vencidosIncluyenMesesAnterioresPeroNoOtrosEstados() {
        val compromisos = listOf(
            compromiso(
                "V1", 10000, hoy.minusMonths(2),
                EstadoCompromiso.VENCIDO
            ),
            compromiso(
                "P1", 10000, hoy.minusDays(1)
            ),
            compromiso(
                "P2", 10000, hoy.minusDays(1),
                EstadoCompromiso.PAGADO
            ),
            compromiso(
                "A1", 10000, hoy.minusDays(1),
                EstadoCompromiso.ANULADO
            ),
            compromiso(
                "R1", 10000, hoy.minusDays(1),
                EstadoCompromiso.EN_REVISION
            ),
            compromiso(
                "F1", 10000, hoy.plusDays(1)
            )
        )

        val resumen = calcularResumenCompromisos(
            compromisos = compromisos,
            importeDe = { it.monto },
            hoy = hoy
        )

        assertEquals(2, resumen.cantidadVencidos)
    }
}
