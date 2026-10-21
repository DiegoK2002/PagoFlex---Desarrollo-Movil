
package cl.fintech.pagoflex.util

import cl.fintech.pagoflex.model.Compromiso
import cl.fintech.pagoflex.model.EstadoCompromiso
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class CalculosTest {

    private val hoy = LocalDate.of(2026, 10, 21)

    private fun compromiso(
        estado: EstadoCompromiso,
        vencimiento: LocalDate
    ) = Compromiso(
        folio = "TEST-001",
        idUsuario = 1L,
        codigoEmpresa = "EMP-01",
        concepto = "Compromiso de prueba",
        numeroCuota = null,
        totalCuotas = null,
        monto = 10000,
        fechaVencimiento = vencimiento,
        estado = estado
    )

    @Test
    fun pendienteConFechaPasadaTieneAtraso() {
        val item = compromiso(
            EstadoCompromiso.PENDIENTE,
            hoy.minusDays(5)
        )

        assertEquals(5, calcularDiasDeAtraso(item, hoy))
    }

    @Test
    fun vencidoConFechaPasadaTieneAtraso() {
        val item = compromiso(
            EstadoCompromiso.VENCIDO,
            hoy.minusDays(8)
        )

        assertEquals(8, calcularDiasDeAtraso(item, hoy))
    }

    @Test
    fun vencimientoDeHoyOFuturoNoTieneAtraso() {
        val venceHoy = compromiso(
            EstadoCompromiso.PENDIENTE,
            hoy
        )

        val venceManana = compromiso(
            EstadoCompromiso.PENDIENTE,
            hoy.plusDays(1)
        )

        assertEquals(0, calcularDiasDeAtraso(venceHoy, hoy))
        assertEquals(0, calcularDiasDeAtraso(venceManana, hoy))
    }

    @Test
    fun estadosNoPagablesNoAcumulanAtraso() {
        val vencimiento = hoy.minusDays(10)

        val estados = listOf(
            EstadoCompromiso.PAGADO,
            EstadoCompromiso.ANULADO,
            EstadoCompromiso.EN_REVISION
        )

        estados.forEach { estado ->
            val item = compromiso(estado, vencimiento)
            assertEquals(
                0,
                calcularDiasDeAtraso(item, hoy)
            )
        }
    }
}
