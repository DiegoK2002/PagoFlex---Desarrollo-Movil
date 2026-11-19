package cl.fintech.pagoflex.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.fintech.pagoflex.data.remote.UsuariosRepository
import cl.fintech.pagoflex.model.EditarPerfilUiState
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

// Edicion de los datos personales del usuario con sesion iniciada
class EditarPerfilViewModel(
    private val repositorio: UsuariosRepository = UsuariosRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditarPerfilUiState())
    val uiState: StateFlow<EditarPerfilUiState> = _uiState.asStateFlow()

    private var usuarioOriginal: Usuario? = null

    // Carga los valores actuales solo la primera vez para no pisar lo que el usuario esta escribiendo
    fun cargar(usuario: Usuario) {
        if (usuarioOriginal != null) {
            return
        }

        usuarioOriginal = usuario
        _uiState.update {
            it.copy(
                nombres = usuario.nombres,
                apellidos = usuario.apellidos,
                correo = usuario.correo,
                comuna = usuario.comuna
            )
        }
    }

    fun onNombresCambia(valor: String) {
        _uiState.update {
            it.copy(nombres = valor, errorNombres = null, errorGeneral = null, guardado = false)
        }
    }

    fun onApellidosCambia(valor: String) {
        _uiState.update {
            it.copy(apellidos = valor, errorApellidos = null, errorGeneral = null, guardado = false)
        }
    }

    fun onCorreoCambia(valor: String) {
        _uiState.update {
            it.copy(correo = valor.trim(), errorCorreo = null, errorGeneral = null, guardado = false)
        }
    }

    fun onComunaSeleccionada(valor: String) {
        _uiState.update {
            it.copy(comuna = valor, errorComuna = null, errorGeneral = null, guardado = false)
        }
    }

    // Indica si hay cambios respecto de los datos guardados
    fun hayCambios(): Boolean {
        val original = usuarioOriginal ?: return false
        val estado = _uiState.value

        return estado.nombres.trim() != original.nombres ||
                estado.apellidos.trim() != original.apellidos ||
                estado.correo.trim() != original.correo ||
                estado.comuna != original.comuna
    }

    private fun validarCampos(): Boolean {
        val estado = _uiState.value

        val errorNombres = Validaciones.validarNombre(estado.nombres, "nombres")
        val errorApellidos = Validaciones.validarNombre(estado.apellidos, "apellidos")
        val errorCorreo = Validaciones.validarCorreo(estado.correo)
        val errorComuna = Validaciones.validarComuna(estado.comuna)

        val hayErrores = listOf(errorNombres, errorApellidos, errorCorreo, errorComuna)
            .any { it != null }

        if (hayErrores) {
            _uiState.update {
                it.copy(
                    errorNombres = errorNombres,
                    errorApellidos = errorApellidos,
                    errorCorreo = errorCorreo,
                    errorComuna = errorComuna,
                    errorGeneral = null
                )
            }
        }

        return !hayErrores
    }

    // onActualizado entrega el usuario con los datos guardados para refrescar la sesion
    fun onGuardar(onActualizado: (Usuario) -> Unit) {
        val original = usuarioOriginal ?: return

        if (_uiState.value.guardando || !validarCampos()) {
            return
        }

        if (!hayCambios()) {
            _uiState.update { it.copy(errorGeneral = "No hay cambios para guardar") }
            return
        }

        val estado = _uiState.value

        _uiState.update { it.copy(guardando = true, errorGeneral = null) }

        viewModelScope.launch {
            try {
                val actualizado = repositorio.actualizarPerfil(
                    actual = original,
                    nombres = estado.nombres,
                    apellidos = estado.apellidos,
                    correo = estado.correo,
                    comuna = estado.comuna
                )

                usuarioOriginal = actualizado
                _uiState.update { it.copy(guardado = true) }
                onActualizado(actualizado)

            } catch (error: CancellationException) {
                throw error

            } catch (error: HttpException) {
                val mensajeServidor = ErroresApi.mensajeServidor(error)

                when (error.code()) {
                    409 -> _uiState.update {
                        it.copy(errorCorreo = "Este correo ya est\u00e1 registrado")
                    }
                    400 -> _uiState.update {
                        it.copy(
                            errorGeneral = mensajeServidor.ifBlank {
                                "El servidor rechaz\u00f3 los datos ingresados"
                            }
                        )
                    }
                    404 -> _uiState.update {
                        it.copy(errorGeneral = "No se encontr\u00f3 la cuenta")
                    }
                    503 -> _uiState.update {
                        it.copy(errorGeneral = "El servicio de usuarios no est\u00e1 disponible")
                    }
                    else -> _uiState.update {
                        it.copy(errorGeneral = "No se pudieron guardar los cambios")
                    }
                }

            } catch (error: IOException) {
                _uiState.update {
                    it.copy(errorGeneral = "No hay conexi\u00f3n con el servidor")
                }

            } catch (error: Exception) {
                _uiState.update {
                    it.copy(errorGeneral = "Ocurri\u00f3 un error al guardar los cambios")
                }

            } finally {
                _uiState.update { it.copy(guardando = false) }
            }
        }
    }
}
