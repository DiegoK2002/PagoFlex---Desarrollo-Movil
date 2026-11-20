package cl.fintech.pagoflex.viewmodel

import cl.fintech.pagoflex.model.Rol
import cl.fintech.pagoflex.model.Usuario
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SesionViewModelTest {

    private fun usuario(nombres: String = "Ana") = Usuario(
        id = 7L,
        rut = "12.345.678-5",
        nombres = nombres,
        apellidos = "Rojas",
        correo = "ana@correo.cl",
        comuna = "Santiago",
        rol = Rol.USUARIO_FINAL
    )

    @Test
    fun iniciarSesionEstableceElUsuarioActual() {
        val viewModel = SesionViewModel()

        viewModel.iniciarSesion(usuario())

        assertEquals(7L, viewModel.usuarioActual.value?.id)
        assertEquals(Rol.USUARIO_FINAL, viewModel.usuarioActual.value?.rol)
    }

    @Test
    fun actualizarUsuarioSustituyeLosDatosDeSesion() {
        val viewModel = SesionViewModel()
        viewModel.iniciarSesion(usuario())

        viewModel.actualizarUsuario(usuario(nombres = "Claudia"))

        assertEquals("Claudia", viewModel.usuarioActual.value?.nombres)
    }
}
