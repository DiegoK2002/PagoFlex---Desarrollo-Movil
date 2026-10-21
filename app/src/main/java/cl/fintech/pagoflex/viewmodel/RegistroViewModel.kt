package cl.fintech.pagoflex.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import cl.fintech.pagoflex.data.PagoFlexDbHelper
import cl.fintech.pagoflex.model.RegistroUiState
import cl.fintech.pagoflex.model.Rol
import cl.fintech.pagoflex.util.Seguridad
import cl.fintech.pagoflex.util.Validaciones
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Logica de la pantalla de registro. Verifica todos los campos (nombres, apellidos, RUT,
 * correo, comuna, contrasena y su repeticion), comprueba que el RUT y el correo no existan
 * ya en la base de datos y, si todo esta correcto, guarda la cuenta.
 */
class RegistroViewModel(application: Application) : AndroidViewModel(application) {

    private val bd = PagoFlexDbHelper.obtener(application)

    private val _uiState = MutableStateFlow(RegistroUiState())
    val uiState: StateFlow<RegistroUiState> = _uiState.asStateFlow()

    // Cada funcion "onXCambia" guarda el valor escrito (con su largo maximo) y borra el error de ese campo.

    fun onNombresCambia(valor: String) {
        _uiState.update { it.copy(nombres = valor.take(40), errorNombres = null) }
    }

    fun onApellidosCambia(valor: String) {
        _uiState.update { it.copy(apellidos = valor.take(40), errorApellidos = null) }
    }

    /** El numero del RUT acepta solo digitos y como maximo 8. */
    fun onRutNumeroCambia(valor: String) {
        _uiState.update { it.copy(rutNumero = valor.filter { c -> c.isDigit() }.take(8), errorRut = null) }
    }

    /** El digito verificador acepta un digito o la letra K, y se guarda en mayuscula. */
    fun onRutDvCambia(valor: String) {
        _uiState.update {
            it.copy(
                rutDv = valor.filter { c -> c.isDigit() || c.uppercaseChar() == 'K' }.take(1).uppercase(),
                errorRut = null
            )
        }
    }

    fun onCorreoCambia(valor: String) {
        _uiState.update { it.copy(correo = valor.trim().take(60), errorCorreo = null) }
    }

    fun onComunaSeleccionada(valor: String) {
        _uiState.update { it.copy(comuna = valor, errorComuna = null) }
    }

    /** Al cambiar la contrasena se compara de inmediato con su repeticion, si ya se escribio. */
    fun onContrasenaCambia(valor: String) {
        val nueva = valor.take(30)
        _uiState.update {
            it.copy(
                contrasena = nueva,
                errorContrasena = null,
                errorRepetir = if (it.repetirContrasena.isNotEmpty() && it.repetirContrasena != nueva) {
                    "Las contrase\u00f1as no coinciden"
                } else {
                    null
                }
            )
        }
    }

    /** Al escribir la repeticion se avisa de inmediato si no coincide con la contrasena. */
    fun onRepetirCambia(valor: String) {
        val nueva = valor.take(30)
        _uiState.update {
            it.copy(
                repetirContrasena = nueva,
                errorRepetir = if (nueva.isNotEmpty() && nueva != it.contrasena) {
                    "Las contrase\u00f1as no coinciden"
                } else {
                    null
                }
            )
        }
    }

    fun onAlternarContrasena() {
        _uiState.update { it.copy(contrasenaVisible = !it.contrasenaVisible) }
    }

    fun onAlternarRepetir() {
        _uiState.update { it.copy(repetirVisible = !it.repetirVisible) }
    }

    /** Verifica todos los campos y, si no hay errores, guarda la cuenta nueva en la base de datos. */
    fun onRegistrar() {
        val e = _uiState.value

        // Verificacion de formato de cada campo
        val errorNombres = Validaciones.validarNombre(e.nombres, "nombres")
        val errorApellidos = Validaciones.validarNombre(e.apellidos, "apellidos")
        var errorRut = Validaciones.validarRut(e.rutNumero, e.rutDv)
        var errorCorreo = Validaciones.validarCorreo(e.correo)
        val errorComuna = Validaciones.validarComuna(e.comuna)
        val errorContrasena = Validaciones.validarContrasena(
            contrasena = e.contrasena,
            correo = e.correo,
            nombres = "${e.nombres} ${e.apellidos}"
        )
        val errorRepetir = Validaciones.validarRepeticion(e.contrasena, e.repetirContrasena)

        // Solo si el formato es correcto se consulta si ya existe en la base de datos
        val rutFormateado = if (errorRut == null) Validaciones.formatearRut(e.rutNumero, e.rutDv) else ""
        if (errorRut == null && bd.existeRut(rutFormateado)) {
            errorRut = "Este RUT ya est\u00e1 registrado"
        }
        if (errorCorreo == null && bd.existeCorreo(e.correo)) {
            errorCorreo = "Este correo ya est\u00e1 registrado"
        }

        val hayErrores = listOf(
            errorNombres, errorApellidos, errorRut, errorCorreo,
            errorComuna, errorContrasena, errorRepetir
        ).any { it != null }

        if (hayErrores) {
            // Se muestran todos los errores a la vez
            _uiState.update {
                it.copy(
                    errorNombres = errorNombres,
                    errorApellidos = errorApellidos,
                    errorRut = errorRut,
                    errorCorreo = errorCorreo,
                    errorComuna = errorComuna,
                    errorContrasena = errorContrasena,
                    errorRepetir = errorRepetir,
                    registroExitoso = false
                )
            }
            return
        }

        // Nombres con la primera letra de cada palabra en mayuscula
        val nombres = Validaciones.normalizarNombre(e.nombres)
        val apellidos = Validaciones.normalizarNombre(e.apellidos)

        val id = bd.registrarUsuario(
            rut = rutFormateado,
            nombres = nombres,
            apellidos = apellidos,
            correo = e.correo,
            contrasenaHash = Seguridad.hashContrasena(e.correo, e.contrasena),
            comuna = e.comuna,
            rol = Rol.USUARIO_FINAL
        )

        if (id == -1L) {
            _uiState.update { it.copy(errorCorreo = "No se pudo guardar el registro. Intenta de nuevo") }
            return
        }

        // Registro correcto: la pantalla muestra el aviso de cuenta creada
        _uiState.update {
            it.copy(
                registroExitoso = true,
                nombreRegistrado = nombres,
                errorNombres = null, errorApellidos = null, errorRut = null, errorCorreo = null,
                errorComuna = null, errorContrasena = null, errorRepetir = null
            )
        }
    }

    /** Vuelve el formulario a su estado inicial. */
    fun onLimpiar() {
        _uiState.value = RegistroUiState()
    }
}
