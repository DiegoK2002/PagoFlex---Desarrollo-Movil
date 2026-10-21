
package cl.fintech.pagoflex.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import cl.fintech.pagoflex.model.Rol
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class ReconocimientoEjemplarDbTest {

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
    fun reconocimientoConservaFechaYUsuario() {
        val fechaOriginal = LocalDate.of(2026, 10, 21)
        val fechaPosterior = LocalDate.of(2026, 10, 22)

        val bd = PagoFlexDbHelper.crearParaPruebas(contexto)

        val idUsuario = bd.registrarUsuario(
            rut = "99.111.111-1",
            nombres = "Usuario",
            apellidos = "Prueba",
            correo = "usuario.prueba@ejemplo.cl",
            contrasenaHash = "hash-prueba",
            comuna = "Santiago",
            rol = Rol.USUARIO_FINAL
        )

        val idOtroUsuario = bd.registrarUsuario(
            rut = "99.222.222-2",
            nombres = "Otro",
            apellidos = "Usuario",
            correo = "otro.usuario@ejemplo.cl",
            contrasenaHash = "hash-prueba",
            comuna = "Santiago",
            rol = Rol.USUARIO_FINAL
        )

        assertTrue(idUsuario > 0)
        assertTrue(idOtroUsuario > 0)

        assertNull(
            bd.fechaReconocimientoEjemplar(idUsuario)
        )

        bd.registrarReconocimientoEjemplar(
            idUsuario,
            fechaOriginal
        )

        bd.close()

        val bdReabierta = PagoFlexDbHelper.crearParaPruebas(contexto)

        try {
            assertEquals(
                fechaOriginal,
                bdReabierta.fechaReconocimientoEjemplar(idUsuario)
            )

            bdReabierta.registrarReconocimientoEjemplar(
                idUsuario,
                fechaPosterior
            )

            assertEquals(
                fechaOriginal,
                bdReabierta.fechaReconocimientoEjemplar(idUsuario)
            )

            assertNull(
                bdReabierta.fechaReconocimientoEjemplar(idOtroUsuario)
            )
        } finally {
            bdReabierta.close()
        }
    }
}
