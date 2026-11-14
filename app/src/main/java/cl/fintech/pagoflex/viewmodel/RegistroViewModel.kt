package cl.fintech.pagoflex.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.fintech.pagoflex.data.remote.ApiClient
import cl.fintech.pagoflex.data.remote.RegistroUsuarioRequest
import cl.fintech.pagoflex.model.RegistroUiState
import cl.fintech.pagoflex.util.Validaciones
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException

class RegistroViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(RegistroUiState())
    val uiState: StateFlow<RegistroUiState> = _uiState.asStateFlow()

    fun onNombresCambia(valor: String) {
        _uiState.update {
            it.copy(nombres = valor.take(40), errorNombres = null, errorGeneral = null)
        }
    }

    fun onApellidosCambia(valor: String) {
        _uiState.update {
            it.copy(apellidos = valor.take(40), errorApellidos = null, errorGeneral = null)
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
                rutDv = valor.filter {
                        c -> c.isDigit() || c.uppercaseChar() == 'K'
                }.take(1).uppercase(),
                errorRut = null,
                errorGeneral = null
            )
        }
    }

    fun onCorreoCambia(valor: String) {
        _uiState.update {
            it.copy(
                correo = valor.trim().take(60),
                errorCorreo = null,
                errorGeneral = null
            )
        }
    }

    fun onComunaSeleccionada(valor: String) {
        _uiState.update {
            it.copy(comuna = valor, errorComuna = null, errorGeneral = null)
        }
    }

    fun onContrasenaCambia(valor: String) {

        val nueva = valor.take(30)

        _uiState.update {
            it.copy(
                contrasena = nueva,
                errorContrasena = null,
                errorGeneral = null,
                errorRepetir = if (
                    it.repetirContrasena.isNotEmpty() &&
                    it.repetirContrasena != nueva
                ) {
                    "Las contraseñas no coinciden"
                } else {
                    null
                }
            )
        }
    }

    fun onRepetirCambia(valor: String) {

        val nueva = valor.take(30)

        _uiState.update {
            it.copy(
                repetirContrasena = nueva,
                errorGeneral = null,
                errorRepetir = if (
                    nueva.isNotEmpty() && nueva != it.contrasena
                ) {
                    "Las contraseñas no coinciden"
                } else {
                    null
                }
            )
        }
    }

    fun onAlternarContrasena() {
        _uiState.update {
            it.copy(contrasenaVisible = !it.contrasenaVisible)
        }
    }

    fun onAlternarRepetir() {
        _uiState.update {
            it.copy(repetirVisible = !it.repetirVisible)
        }
    }

    fun onRegistrar() {

        val estado = _uiState.value

        if (estado.registrando) {
            return
        }

        val errorNombres = Validaciones.validarNombre(
            estado.nombres,
            "nombres"
        )

        val errorApellidos = Validaciones.validarNombre(
            estado.apellidos,
            "apellidos"
        )

        val errorRut = Validaciones.validarRut(
            estado.rutNumero,
            estado.rutDv
        )

        val errorCorreo = Validaciones.validarCorreo(
            estado.correo
        )

        val errorComuna = Validaciones.validarComuna(
            estado.comuna
        )

        val errorContrasena = Validaciones.validarContrasena(
            contrasena = estado.contrasena,
            correo = estado.correo,
            nombres = "${estado.nombres} ${estado.apellidos}"
        )

        val errorRepetir = Validaciones.validarRepeticion(
            estado.contrasena,
            estado.repetirContrasena
        )

        val hayErrores = listOf(
            errorNombres,
            errorApellidos,
            errorRut,
            errorCorreo,
            errorComuna,
            errorContrasena,
            errorRepetir
        ).any { it != null }

        if (hayErrores) {

            _uiState.update {
                it.copy(
                    errorNombres = errorNombres,
                    errorApellidos = errorApellidos,
                    errorRut = errorRut,
                    errorCorreo = errorCorreo,
                    errorComuna = errorComuna,
                    errorContrasena = errorContrasena,
                    errorRepetir = errorRepetir,
                    errorGeneral = null,
                    registroExitoso = false
                )
            }

            return
        }

        val rutFormateado = Validaciones.formatearRut(
            estado.rutNumero,
            estado.rutDv
        )

        val nombres = Validaciones.normalizarNombre(estado.nombres)
        val apellidos = Validaciones.normalizarNombre(estado.apellidos)

        _uiState.update {
            it.copy(
                registrando = true,
                registroExitoso = false,
                errorGeneral = null,
                errorRut = null,
                errorCorreo = null
            )
        }

        viewModelScope.launch {

            try {

                val usuario = ApiClient.usuariosApi.registrarUsuario(
                    RegistroUsuarioRequest(
                        rut = rutFormateado,
                        nombres = nombres,
                        apellidos = apellidos,
                        correo = estado.correo,
                        contrasena = estado.contrasena,
                        comuna = estado.comuna
                    )
                )

                _uiState.update {
                    it.copy(
                        registroExitoso = true,
                        nombreRegistrado = usuario.nombres,
                        errorGeneral = null,
                        errorNombres = null,
                        errorApellidos = null,
                        errorRut = null,
                        errorCorreo = null,
                        errorComuna = null,
                        errorContrasena = null,
                        errorRepetir = null
                    )
                }

            } catch (error: HttpException) {

                val mensajeServidor = try {
                    JSONObject(
                        error.response()?.errorBody()?.string().orEmpty()
                    ).optString("mensaje")
                } catch (excepcion: Exception) {
                    ""
                }

                when (error.code()) {

                    409 -> {

                        when {

                            mensajeServidor.contains("RUT", ignoreCase = true) -> {
                                _uiState.update {
                                    it.copy(errorRut = "Este RUT ya está registrado")
                                }
                            }

                            mensajeServidor.contains("correo", ignoreCase = true) -> {
                                _uiState.update {
                                    it.copy(errorCorreo = "Este correo ya está registrado")
                                }
                            }

                            else -> {
                                _uiState.update {
                                    it.copy(
                                        errorGeneral = "El RUT o correo ya está registrado"
                                    )
                                }
                            }
                        }
                    }

                    400 -> {
                        _uiState.update {
                            it.copy(
                                errorGeneral = mensajeServidor.ifBlank {
                                    "El servidor rechazó los datos del registro"
                                }
                            )
                        }
                    }

                    503 -> {
                        _uiState.update {
                            it.copy(
                                errorGeneral = "El servicio de usuarios no está disponible"
                            )
                        }
                    }

                    else -> {
                        _uiState.update {
                            it.copy(
                                errorGeneral = "No se pudo completar el registro"
                            )
                        }
                    }
                }

            } catch (error: IOException) {

                _uiState.update {
                    it.copy(
                        errorGeneral = "No se pudo conectar con el servidor"
                    )
                }

            } catch (error: CancellationException) {
                throw error

            } catch (error: Exception) {

                _uiState.update {
                    it.copy(
                        errorGeneral = "Ocurrió un error al registrar la cuenta"
                    )
                }

            } finally {

                _uiState.update {
                    it.copy(registrando = false)
                }
            }
        }
    }

    fun onLimpiar() {

        if (!_uiState.value.registrando) {
            _uiState.value = RegistroUiState()
        }
    }
}
