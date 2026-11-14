package cl.fintech.pagoflex.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.fintech.pagoflex.data.PagoFlexDbHelper
import cl.fintech.pagoflex.data.remote.ApiClient
import cl.fintech.pagoflex.data.remote.LoginRequest
import cl.fintech.pagoflex.model.Rol
import cl.fintech.pagoflex.model.SesionUiState
import cl.fintech.pagoflex.model.Usuario
import cl.fintech.pagoflex.util.Seguridad
import cl.fintech.pagoflex.util.Validaciones
import java.io.IOException
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

class InicioSesionViewModel(application: Application) : AndroidViewModel(application) {

    private val bd = PagoFlexDbHelper.obtener(application)

    private val _uiState = MutableStateFlow(SesionUiState())
    val uiState: StateFlow<SesionUiState> = _uiState.asStateFlow()

    private var solicitudEnCurso = false

    fun onCorreoCambia(valor: String) {
        _uiState.update {
            it.copy(
                correo = valor.trim(),
                errorCorreo = null,
                errorGeneral = null
            )
        }
    }

    fun onContrasenaCambia(valor: String) {
        _uiState.update {
            it.copy(
                contrasena = valor,
                errorContrasena = null,
                errorGeneral = null
            )
        }
    }

    fun onAlternarVisibilidad() {
        _uiState.update {
            it.copy(contrasenaVisible = !it.contrasenaVisible)
        }
    }

    fun onIniciar(): Usuario? {

        if (!validarCampos()) {
            return null
        }

        val estado = _uiState.value

        val hash = Seguridad.hashContrasena(
            estado.correo,
            estado.contrasena
        )

        val usuario = bd.buscarPorCredenciales(
            estado.correo,
            hash
        )

        if (usuario == null) {
            _uiState.update {
                it.copy(
                    errorGeneral = "Correo o contraseña incorrectos"
                )
            }
        }

        return usuario
    }

    fun onIniciarRemoto(onInicioExitoso: (Usuario) -> Unit) {

        if (solicitudEnCurso || !validarCampos()) {
            return
        }

        val estado = _uiState.value

        solicitudEnCurso = true

        _uiState.update {
            it.copy(errorGeneral = null)
        }

        viewModelScope.launch {

            try {

                val respuesta = ApiClient.usuariosApi.iniciarSesion(
                    LoginRequest(
                        correo = estado.correo,
                        contrasena = estado.contrasena
                    )
                )

                if (!respuesta.activo) {
                    _uiState.update {
                        it.copy(
                            errorGeneral = "La cuenta está desactivada"
                        )
                    }
                    return@launch
                }

                val rol = when (
                    respuesta.rol.trim().uppercase(Locale.ROOT)
                ) {
                    "USUARIO_FINAL" -> Rol.USUARIO_FINAL
                    "AGENTE" -> Rol.AGENTE
                    else -> {
                        _uiState.update {
                            it.copy(
                                errorGeneral = "El rol del usuario no es compatible"
                            )
                        }
                        return@launch
                    }
                }

                val usuario = Usuario(
                    id = respuesta.idUsuario,
                    rut = respuesta.rut,
                    nombres = respuesta.nombres,
                    apellidos = respuesta.apellidos,
                    correo = respuesta.correo,
                    comuna = respuesta.comuna,
                    rol = rol
                )

                onInicioExitoso(usuario)

            } catch (error: HttpException) {

                val mensaje = when (error.code()) {
                    400 -> "Los datos ingresados no son válidos"
                    401 -> "Correo o contraseña incorrectos"
                    403 -> "La cuenta está desactivada"
                    503 -> "El servicio de usuarios no está disponible"
                    else -> "No se pudo iniciar sesión"
                }

                _uiState.update {
                    it.copy(errorGeneral = mensaje)
                }


            } catch (error: IOException) {

                android.util.Log.e(
                    "PagoFlexLogin",
                    "Error de conexion con el backend",
                    error
                )

                _uiState.update {
                    it.copy(
                        errorGeneral = "Error de conexión: ${error.message ?: error.javaClass.simpleName}"
                    )
                }


            } catch (error: Exception) {

                _uiState.update {
                    it.copy(
                        errorGeneral = "Ocurrió un error al iniciar sesión"
                    )
                }

            } finally {

                solicitudEnCurso = false
            }
        }
    }

    private fun validarCampos(): Boolean {

        val estado = _uiState.value

        val errorCorreo = Validaciones.validarCorreo(
            estado.correo
        )

        val errorContrasena = if (estado.contrasena.isEmpty()) {
            "Ingresa tu contraseña"
        } else {
            null
        }

        if (errorCorreo != null || errorContrasena != null) {

            _uiState.update {
                it.copy(
                    errorCorreo = errorCorreo,
                    errorContrasena = errorContrasena,
                    errorGeneral = null
                )
            }

            return false
        }

        return true
    }

    fun onLimpiar() {
        if (!solicitudEnCurso) {
            _uiState.value = SesionUiState()
        }
    }
}
