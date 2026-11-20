package cl.fintech.pagoflex.viewmodel

import cl.fintech.pagoflex.data.remote.UsuariosRepository
import cl.fintech.pagoflex.util.Validaciones
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Test

class RecuperarContrasenaViewModelTest {

    private fun crearViewModel() = RecuperarContrasenaViewModel(
        UsuariosRepository(api = UsuariosApiFalsa())
    )

    @Test
    fun filtraElRutYLlevaDigitoVerificadorAMayuscula() {
        val viewModel = crearViewModel()

        viewModel.onRutNumeroCambia("12.345.678-9")
        viewModel.onRutDvCambia("**k7")

        assertEquals("12345678", viewModel.uiState.value.rutNumero)
        assertEquals("K", viewModel.uiState.value.rutDv)
    }

    @Test
    fun rechazaSolicitudConCamposVacios() {
        val viewModel = crearViewModel()

        viewModel.onRecuperar()

        assertNotNull(viewModel.uiState.value.errorCorreo)
        assertNotNull(viewModel.uiState.value.errorRut)
        assertNotNull(viewModel.uiState.value.errorContrasena)
        assertFalse(viewModel.uiState.value.enviando)
    }

    @Test
    fun rechazaContrasenasQueNoCoinciden() {
        val viewModel = crearViewModel()
        viewModel.onCorreoCambia("b@b.cl")
        viewModel.onRutNumeroCambia("12345678")
        viewModel.onRutDvCambia(Validaciones.calcularDigitoVerificador("12345678").toString())
        viewModel.onContrasenaCambia("ClaveNueva9!")
        viewModel.onRepetirCambia("OtraClave8!")

        viewModel.onRecuperar()

        assertEquals(
            "Las contraseñas no coinciden",
            viewModel.uiState.value.errorRepetir
        )
        assertFalse(viewModel.uiState.value.enviando)
    }
}
