package cl.fintech.pagoflex.viewmodel

import cl.fintech.pagoflex.data.remote.ActualizarUsuarioRequest
import cl.fintech.pagoflex.data.remote.CambiarContrasenaRequest
import cl.fintech.pagoflex.data.remote.LoginRequest
import cl.fintech.pagoflex.data.remote.RecuperarContrasenaRequest
import cl.fintech.pagoflex.data.remote.RegistroUsuarioRequest
import cl.fintech.pagoflex.data.remote.UsuarioResponse
import cl.fintech.pagoflex.data.remote.UsuariosApi
import cl.fintech.pagoflex.data.remote.UsuariosRepository
import cl.fintech.pagoflex.model.Rol
import cl.fintech.pagoflex.model.Usuario
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

internal class UsuariosApiFalsa : UsuariosApi {
    override suspend fun listarUsuarios(): List<UsuarioResponse> = error("No debe llamar a la API")
    override suspend fun iniciarSesion(datos: LoginRequest): UsuarioResponse = error("No debe llamar a la API")
    override suspend fun registrarUsuario(datos: RegistroUsuarioRequest): UsuarioResponse = error("No debe llamar a la API")
    override suspend fun desactivarUsuario(idUsuario: Long) { error("No debe llamar a la API") }
    override suspend fun actualizarUsuario(idUsuario: Long, datos: ActualizarUsuarioRequest): UsuarioResponse = error("No debe llamar a la API")
    override suspend fun cambiarContrasena(idUsuario: Long, datos: CambiarContrasenaRequest) { error("No debe llamar a la API") }
    override suspend fun recuperarContrasena(datos: RecuperarContrasenaRequest) { error("No debe llamar a la API") }
}

class EditarPerfilViewModelTest {

    private fun crearViewModel() = EditarPerfilViewModel(
        UsuariosRepository(api = UsuariosApiFalsa())
    )

    private fun usuario() = Usuario(
        id = 7L,
        rut = "12.345.678-5",
        nombres = "Ana",
        apellidos = "Rojas",
        correo = "ana@correo.cl",
        comuna = "Santiago",
        rol = Rol.USUARIO_FINAL
    )

    @Test
    fun cargarUsuarioCompletaCamposSinMarcarCambios() {
        val viewModel = crearViewModel()

        viewModel.cargar(usuario())

        assertEquals("Ana", viewModel.uiState.value.nombres)
        assertEquals("ana@correo.cl", viewModel.uiState.value.correo)
        assertFalse(viewModel.hayCambios())
    }

    @Test
    fun cambiarCorreoDetectaModificacion() {
        val viewModel = crearViewModel()
        viewModel.cargar(usuario())

        viewModel.onCorreoCambia("  nuevo@correo.cl  ")

        assertEquals("nuevo@correo.cl", viewModel.uiState.value.correo)
        assertTrue(viewModel.hayCambios())
    }
}
