package cl.fintech.pagoflex.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.fintech.pagoflex.data.remote.UsuariosRepository
import cl.fintech.pagoflex.model.RecuperarContrasenaUiState
import cl.fintech.pagoflex.util.ErroresApi
import cl.fintech.pagoflex.util.Validaciones
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

// Flujo para restablecer la contrasena sin haber iniciado sesion.
// La identidad se comprueba con el correo y el RUT registrados.
class RecuperarContrasenaViewModel(
    private val repositorio: UsuariosRepository = UsuariosRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecuperarContrasenaUiState())
    val uiState: StateFlow<RecuperarContrasenaUiState> = _uiState.asStateFlow()

    fun onCorreoCambia(valor: String) {
        _uiState.update {
            it.copy(correo = valor.trim(), errorCorreo = null, errorGeneral = null)
        }
    }

    fun onRutNumeroCambia(valor: String) {
        _uiState.update {
            it.copy(
                rutNumero = valor.filter { c -> c.isDigit() }.take(8),
                errorRut = null,
                errorGeneral = null
            )
        }
    }

    fun onRutDvCambia(valor: String) {
        _uiState.update {
            it.copy(
                rutDv = valor.filter { c -> c.isDigit() || c.uppercaseChar() == 'K' }
                    .take(1)
                    .uppercase(),
                errorRut = null,
                errorGeneral = null
            )
        }
    }

    fun onContrasenaCambia(valor: String) {
        _uiState.update {
            it.copy(contrasenaNueva = valor, errorContrasena = null, errorGeneral = null)
        }
    }

    fun onRepetirCambia(valor: String) {
        _uiState.update {
            it.copy(repetirContrasena = valor, errorRepetir = null, errorGeneral = null)
        }
    }

    fun onAlternarContrasena() {
        _uiState.update { it.copy(contrasenaVisible = !it.contrasenaVisible) }
    }

    fun onAlternarRepetir() {
        _uiState.update { it.copy(repetirVisible = !it.repetirVisible) }
    }

    // Valida todos los campos y marca los errores encontrados; devuelve true si no hay ninguno
    private fun validarCampos(): Boolean {
        val estado = _uiState.value

        val errorCorreo = Validaciones.validarCorreo(estado.correo)
        val errorRut = Validaciones.validarRut(estado.rutNumero, estado.rutDv)
        val errorContrasena = Validaciones.validarContrasena(
            contrasena = estado.contrasenaNueva,
            correo = estado.correo
        )
        val errorRepetir = Validaciones.validarRepeticion(
            estado.contrasenaNueva,
            estado.repetirContrasena
        )

        val hayErrores = listOf(errorCorreo, errorRut, errorContrasena, errorRepetir)
            .any { it != null }

        if (hayErrores) {
            _uiState.update {
                it.copy(
                    errorCorreo = errorCorreo,
                    errorRut = errorRut,
                    errorContrasena = errorContrasena,
                    errorRepetir = errorRepetir,
                    errorGeneral = null
                )
            }
        }

        return !hayErrores
    }

    fun onRecuperar() {
        // Evita envios duplicados mientras hay una solicitud en curso
        if (_uiState.value.enviando || !validarCampos()) {
            return
        }

        val estado = _uiState.value
        val rutFormateado = Validaciones.formatearRut(estado.rutNumero, estado.rutDv)

        _uiState.update { it.copy(enviando = true, errorGeneral = null) }

        viewModelScope.launch {
            try {
                repositorio.recuperarContrasena(
                    correo = estado.correo,
                    rut = rutFormateado,
                    contrasenaNueva = estado.contrasenaNueva
                )

                _uiState.update {
                    it.copy(
                        recuperada = true,
                        contrasenaNueva = "",
                        repetirContrasena = ""
                    )
                }

            } catch (error: CancellationException) {
                throw error

            } catch (error: HttpException) {
                val mensajeServidor = ErroresApi.mensajeServidor(error)

                val mensaje = when (error.code()) {
                    400 -> mensajeServidor.ifBlank { "Los datos ingresados no son v\u00e1lidos" }
                    404 -> "El correo y el RUT no coinciden con una cuenta activa"
                    503 -> "El servicio de usuarios no est\u00e1 disponible"
                    else -> "No se pudo restablecer la contrase\u00f1a"
                }
                _uiState.update { it.copy(errorGeneral = mensaje) }

            } catch (error: IOException) {
                _uiState.update {
                    it.copy(errorGeneral = "No hay conexi\u00f3n con el servidor")
                }

            } catch (error: Exception) {
                _uiState.update {
                    it.copy(errorGeneral = "Ocurri\u00f3 un error al restablecer la contrase\u00f1a")
                }

            } finally {
                _uiState.update { it.copy(enviando = false) }
            }
        }
    }
}
