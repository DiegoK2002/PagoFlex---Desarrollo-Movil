package cl.fintech.pagoflex.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import cl.fintech.pagoflex.data.PagoFlexDbHelper
import cl.fintech.pagoflex.model.SesionUiState
import cl.fintech.pagoflex.model.Usuario
import cl.fintech.pagoflex.util.Seguridad
import cl.fintech.pagoflex.util.Validaciones
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Logica de la pantalla de inicio de sesion: guarda lo escrito, verifica los campos y busca
 * la cuenta en la base de datos. El rol no se elige: viene de la cuenta guardada.
 * Si las credenciales no coinciden se muestra un mensaje general, sin indicar si fallo el
 * correo o la contrasena, para no entregar informacion a quien intente adivinarlas.
 */
class InicioSesionViewModel(application: Application) : AndroidViewModel(application) {

    // Acceso a la base de datos SQLite
    private val bd = PagoFlexDbHelper.obtener(application)

    private val _uiState = MutableStateFlow(SesionUiState())
    val uiState: StateFlow<SesionUiState> = _uiState.asStateFlow()

    /** Guarda el correo escrito y borra los errores anteriores. */
    fun onCorreoCambia(valor: String) {
        _uiState.update {
            it.copy(correo = valor.trim(), errorCorreo = null, errorGeneral = null)
        }
    }

    /** Guarda la contrasena escrita y borra los errores anteriores. */
    fun onContrasenaCambia(valor: String) {
        _uiState.update {
            it.copy(contrasena = valor, errorContrasena = null, errorGeneral = null)
        }
    }

    /** Muestra u oculta la contrasena. */
    fun onAlternarVisibilidad() {
        _uiState.update { it.copy(contrasenaVisible = !it.contrasenaVisible) }
    }

    /** Intenta iniciar sesion. Devuelve la cuenta si fue correcto; si no, devuelve null y deja los errores en pantalla. */
    fun onIniciar(): Usuario? {
        val estado = _uiState.value

        // Verificacion de los campos antes de consultar la base de datos
        val errorCorreo = Validaciones.validarCorreo(estado.correo)
        val errorContrasena = if (estado.contrasena.isEmpty()) "Ingresa tu contrase\u00f1a" else null

        if (errorCorreo != null || errorContrasena != null) {
            _uiState.update {
                it.copy(errorCorreo = errorCorreo, errorContrasena = errorContrasena, errorGeneral = null)
            }
            return null
        }

        // Se calcula el hash de lo escrito y se busca una cuenta que coincida
        val hash = Seguridad.hashContrasena(estado.correo, estado.contrasena)
        val usuario = bd.buscarPorCredenciales(estado.correo, hash)

        if (usuario == null) {
            _uiState.update { it.copy(errorGeneral = "Correo o contrase\u00f1a incorrectos") }
            return null
        }
        return usuario
    }

    /** Vuelve el formulario a su estado inicial. */
    fun onLimpiar() {
        _uiState.value = SesionUiState()
    }
}
