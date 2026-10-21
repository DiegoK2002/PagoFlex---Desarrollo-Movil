
package cl.fintech.pagoflex.data

import android.content.ContentValues
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import cl.fintech.pagoflex.model.EstadoCompromiso
import cl.fintech.pagoflex.model.Pago
import cl.fintech.pagoflex.model.Rol
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
class RegistrarPagoDbTest {

    private val contexto
        get() = InstrumentationRegistry
            .getInstrumentation()
            .targetContext

    private val nombreBase = "pagoflex_reconocimientos_test.db"

    @Before
    fun preparar() {
        contexto.deleteDatabase(nombreBase)
    }

    @After
    fun terminar() {
        contexto.deleteDatabase(nombreBase)
    }

    @Test
    fun pagoProtegePropietarioYEvitaDuplicados() {
        val bd = PagoFlexDbHelper.crearParaPruebas(contexto)

        try {
            val titular = bd.registrarUsuario(
                rut = "99.333.333-3",
                nombres = "Titular",
                apellidos = "Prueba",
                correo = "titular.pago@ejemplo.cl",
                contrasenaHash = "hash-prueba",
                comuna = "Santiago",
                rol = Rol.USUARIO_FINAL
            )

            val otroUsuario = bd.registrarUsuario(
                rut = "99.444.444-4",
                nombres = "Otro",
                apellidos = "Usuario",
                correo = "otro.pago@ejemplo.cl",
                contrasenaHash = "hash-prueba",
                comuna = "Santiago",
                rol = Rol.USUARIO_FINAL
            )

            assertTrue(titular > 0L)
            assertTrue(otroUsuario > 0L)

            val folio = "TEST-PAGO-001"

            val valores = ContentValues().apply {
                put("folio", folio)
                put("id_usuario", titular)
                put("codigo_empresa", "EC-01")
                put("concepto", "Compromiso de prueba")
                putNull("numero_cuota")
                putNull("total_cuotas")
                put("monto", 10000)
                put(
                    "fecha_vencimiento",
                    LocalDate.now().plusDays(7).toString()
                )
                put("estado", EstadoCompromiso.PENDIENTE.name)
            }

            val idCompromiso = bd.writableDatabase.insertOrThrow(
                "compromisos",
                null,
                valores
            )

            assertTrue(idCompromiso > 0L)

            val pagoAjeno = Pago(
                numeroComprobante = "CPR-990001",
                folioCompromiso = folio,
                idUsuario = otroUsuario,
                codigoEmpresa = "EC-01",
                concepto = "Compromiso de prueba",
                fechaHora = LocalDateTime.now(),
                montoPagado = 10000,
                canal = "App",
                aTiempo = true
            )

            assertFalse(bd.registrarPago(pagoAjeno))

            assertNull(
                bd.pagoPorFolioDeUsuario(folio, otroUsuario)
            )

            assertTrue(
                bd.pagosDeUsuario(otroUsuario).isEmpty()
            )

            assertEquals(
                EstadoCompromiso.PENDIENTE,
                bd.compromisosDeUsuario(titular)
                    .first { it.folio == folio }
                    .estado
            )

            val pagoValido = pagoAjeno.copy(
                numeroComprobante = "CPR-990002",
                idUsuario = titular
            )

            assertTrue(bd.registrarPago(pagoValido))

            assertEquals(
                EstadoCompromiso.PAGADO,
                bd.compromisosDeUsuario(titular)
                    .first { it.folio == folio }
                    .estado
            )

            assertNotNull(
                bd.pagoPorFolioDeUsuario(folio, titular)
            )

            val pagoDuplicado = pagoValido.copy(
                numeroComprobante = "CPR-990003"
            )

            assertFalse(bd.registrarPago(pagoDuplicado))

            assertEquals(
                1,
                bd.pagosDeUsuario(titular)
                    .count { it.folioCompromiso == folio }
            )
        } finally {
            bd.close()
        }
    }
}
