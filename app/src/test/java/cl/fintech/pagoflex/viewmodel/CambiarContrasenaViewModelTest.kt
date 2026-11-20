package cl.fintech.pagoflex.viewmodel

import cl.fintech.pagoflex.data.remote.UsuariosRepository
import cl.fintech.pagoflex.model.Rol
import cl.fintech.pagoflex.model.Usuario
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Test

class CambiarContrasenaViewModelTest {

    private fun crearViewModel() = CambiarContrasenaViewModel(
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
    fun rechazaContrasenaActualVacia() {
        val viewModel = crearViewModel()

        viewModel.onCambiar(usuario())

        assertNotNull(viewModel.uiState.value.errorActual)
        assertFalse(viewModel.uiState.value.guardando)
    }

    @Test
    fun rechazaNuevaContrasenaIgualALaActual() {
        val viewModel = crearViewModel()
        viewModel.onActualCambia("ClaveActual9!")
        viewModel.onNuevaCambia("ClaveActual9!")
        viewModel.onRepetirCambia("ClaveActual9!")

        viewModel.onCambiar(usuario())

        assertEquals(
            "La nueva contraseña debe ser distinta de la actual",
            viewModel.uiState.value.errorNueva
        )
        assertFalse(viewModel.uiState.value.guardando)
    }

    @Test
    fun rechazaConfirmacionDeContrasenaDiferente() {
        val viewModel = crearViewModel()
        viewModel.onActualCambia("ClaveActual9!")
        viewModel.onNuevaCambia("ClaveNueva8!")
        viewModel.onRepetirCambia("OtraClave8!")

        viewModel.onCambiar(usuario())

        assertEquals(
            "Las contraseñas no coinciden",
            viewModel.uiState.value.errorRepetir
        )
        assertFalse(viewModel.uiState.value.guardando)
    }
}
