package cl.fintech.pagoflex.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.fintech.pagoflex.data.remote.UsuariosRepository
import cl.fintech.pagoflex.model.CambiarContrasenaUiState
import cl.fintech.pagoflex.model.Usuario
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

class CambiarContrasenaViewModel(
    private val repositorio: UsuariosRepository = UsuariosRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CambiarContrasenaUiState())
    val uiState: StateFlow<CambiarContrasenaUiState> = _uiState.asStateFlow()

    fun onActualCambia(valor: String) {
        _uiState.update {
            it.copy(contrasenaActual = valor, errorActual = null, errorGeneral = null, cambiada = false)
        }
    }

    fun onNuevaCambia(valor: String) {
        _uiState.update {
            it.copy(contrasenaNueva = valor, errorNueva = null, errorGeneral = null, cambiada = false)
        }
    }

    fun onRepetirCambia(valor: String) {
        _uiState.update {
            it.copy(repetirContrasena = valor, errorRepetir = null, errorGeneral = null, cambiada = false)
        }
    }

    fun onAlternarActual() {
        _uiState.update { it.copy(actualVisible = !it.actualVisible) }
    }

    fun onAlternarNueva() {
        _uiState.update { it.copy(nuevaVisible = !it.nuevaVisible) }
    }

    fun onAlternarRepetir() {
        _uiState.update { it.copy(repetirVisible = !it.repetirVisible) }
    }

    private fun validarCampos(usuario: Usuario): Boolean {
        val estado = _uiState.value

        val errorActual = if (estado.contrasenaActual.isEmpty()) {
            "Ingresa tu contrase\u00f1a actual"
        } else {
            null
        }

        val errorNueva = Validaciones.validarContrasena(
            contrasena = estado.contrasenaNueva,
            correo = usuario.correo,
            nombres = usuario.nombres
        ) ?: if (estado.contrasenaNueva == estado.contrasenaActual) {
            "La nueva contrase\u00f1a debe ser distinta de la actual"
        } else {
            null
        }

        val errorRepetir = Validaciones.validarRepeticion(
            estado.contrasenaNueva,
            estado.repetirContrasena
        )

        val hayErrores = listOf(errorActual, errorNueva, errorRepetir).any { it != null }

        if (hayErrores) {
            _uiState.update {
                it.copy(
                    errorActual = errorActual,
                    errorNueva = errorNueva,
                    errorRepetir = errorRepetir,
                    errorGeneral = null
                )
            }
        }

        return !hayErrores
    }

    fun onCambiar(usuario: Usuario) {
        if (_uiState.value.guardando || !validarCampos(usuario)) {
            return
        }

        val estado = _uiState.value

        _uiState.update { it.copy(guardando = true, errorGeneral = null) }

        viewModelScope.launch {
            try {
                repositorio.cambiarContrasena(
                    idUsuario = usuario.id,
                    contrasenaActual = estado.contrasenaActual,
                    contrasenaNueva = estado.contrasenaNueva
                )

                _uiState.value = CambiarContrasenaUiState(cambiada = true)

            } catch (error: CancellationException) {
                throw error

            } catch (error: HttpException) {
                val mensajeServidor = ErroresApi.mensajeServidor(error)

                when (error.code()) {
                    401 -> _uiState.update {
                        it.copy(errorActual = "La contrase\u00f1a actual es incorrecta")
                    }
                    403 -> _uiState.update {
                        it.copy(errorGeneral = "La cuenta est\u00e1 desactivada")
                    }
                    400 -> _uiState.update {
                        it.copy(
                            errorGeneral = mensajeServidor.ifBlank {
                                "El servidor rechaz\u00f3 la nueva contrase\u00f1a"
                            }
                        )
                    }
                    503 -> _uiState.update {
                        it.copy(errorGeneral = "El servicio de usuarios no est\u00e1 disponible")
                    }
                    else -> _uiState.update {
                        it.copy(errorGeneral = "No se pudo cambiar la contrase\u00f1a")
                    }
                }

            } catch (error: IOException) {
                _uiState.update {
                    it.copy(errorGeneral = "No hay conexi\u00f3n con el servidor")
                }

            } catch (error: Exception) {
                _uiState.update {
                    it.copy(errorGeneral = "Ocurri\u00f3 un error al cambiar la contrase\u00f1a")
                }

            } finally {
                _uiState.update { it.copy(guardando = false) }
            }
        }
    }
}
