package cl.fintech.pagoflex.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.fintech.pagoflex.data.remote.AdministradorRepository
import cl.fintech.pagoflex.data.remote.CrearCompromisoRequest
import cl.fintech.pagoflex.data.remote.EmpresaRemota
import cl.fintech.pagoflex.data.remote.UsuarioResponse
import cl.fintech.pagoflex.data.remote.CompromisoRemoto
import cl.fintech.pagoflex.data.remote.ActualizarUsuarioRequest
import cl.fintech.pagoflex.data.remote.RegistroUsuarioRequest
import java.math.BigDecimal
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job

class AdministradorViewModel : ViewModel() {

    private val repositorio = AdministradorRepository()

    private val _usuarios =
        MutableStateFlow<List<UsuarioResponse>>(emptyList())
    val usuarios = _usuarios.asStateFlow()

    private val _empresas =
        MutableStateFlow<List<EmpresaRemota>>(emptyList())
    val empresas = _empresas.asStateFlow()


    private val _compromisos =
        MutableStateFlow<List<CompromisoRemoto>>(emptyList())
    val compromisos = _compromisos.asStateFlow()

    private val _cargandoCompromisos = MutableStateFlow(false)
    val cargandoCompromisos = _cargandoCompromisos.asStateFlow()

    private var consultaCompromisosJob: Job? = null

    private var idUsuarioConsultaActual: Long? = null

    private val _cargando = MutableStateFlow(false)
    val cargando = _cargando.asStateFlow()

    private val _guardando = MutableStateFlow(false)
    val guardando = _guardando.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje = _mensaje.asStateFlow()

    fun cargarDatos() {
        if (_cargando.value) return

        viewModelScope.launch {
            _cargando.value = true
            _mensaje.value = null


            try {
                _usuarios.value = repositorio.listarUsuarios()
                _empresas.value = repositorio.listarEmpresas()
            }
            catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _mensaje.value =
                    "No se pudieron cargar los datos."
            } finally {
                _cargando.value = false
            }
        }
    }

    fun asignarCompromiso(
        idUsuario: Long,
        idEmpresa: Long,
        nombre: String,
        monto: BigDecimal,
        fechaVencimiento: String
    ) {
        if (_guardando.value) return

        if (
            idUsuario <= 0L ||
            idEmpresa <= 0L ||
            nombre.isBlank() ||
            monto <= BigDecimal.ZERO ||
            fechaVencimiento.isBlank()
        ) {
            _mensaje.value = "Completa todos los campos."
            return
        }

        viewModelScope.launch {
            _guardando.value = true
            _mensaje.value = null

            try {
                val compromisoCreado = repositorio.asignarCompromiso(
                    CrearCompromisoRequest(
                        idUsuario = idUsuario,
                        idEmpresa = idEmpresa,
                        nombre = nombre.trim(),
                        monto = monto,
                        fechaVencimiento = fechaVencimiento
                    )
                )

                if (idUsuarioConsultaActual == compromisoCreado.idUsuario) {
                    _compromisos.value =
                        listOf(compromisoCreado) +
                                _compromisos.value.filterNot {
                                    it.idCompromiso == compromisoCreado.idCompromiso
                                }
                }

                _mensaje.value =
                    "Compromiso asignado correctamente."


            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _mensaje.value =
                    "No se pudo asignar el compromiso."
            } finally {
                _guardando.value = false
            }
        }
    }


    fun actualizarCompromiso(
        idCompromiso: Long,
        idUsuario: Long,
        idEmpresa: Long,
        nombre: String,
        monto: BigDecimal,
        fechaVencimiento: String
    ) {
        if (_guardando.value) return

        if (
            idCompromiso <= 0L ||
            idUsuario <= 0L ||
            idEmpresa <= 0L ||
            nombre.isBlank() ||
            monto <= BigDecimal.ZERO ||
            fechaVencimiento.isBlank()
        ) {
            _mensaje.value = "Completa todos los campos."
            return
        }

        viewModelScope.launch {
            _guardando.value = true
            _mensaje.value = null

            try {
                val compromisoActualizado =
                    repositorio.actualizarCompromiso(
                        idCompromiso = idCompromiso,
                        datos = CrearCompromisoRequest(
                            idUsuario = idUsuario,
                            idEmpresa = idEmpresa,
                            nombre = nombre.trim(),
                            monto = monto,
                            fechaVencimiento = fechaVencimiento
                        )
                    )

                _compromisos.value = _compromisos.value.mapNotNull { compromiso ->
                    if (compromiso.idCompromiso != idCompromiso) {
                        compromiso
                    } else {
                        compromisoActualizado.takeIf {
                            it.idUsuario == idUsuarioConsultaActual
                        }
                    }
                }

                _mensaje.value =
                    "Compromiso actualizado correctamente."

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _mensaje.value =
                    "No se pudo actualizar el compromiso."
            } finally {
                _guardando.value = false
            }
        }
    }




    fun cargarCompromisosPorUsuario(idUsuario: Long) {
        consultaCompromisosJob?.cancel()

        idUsuarioConsultaActual = idUsuario.takeIf { it > 0L }
        _compromisos.value = emptyList()
        _cargandoCompromisos.value = idUsuario > 0L

        if (idUsuario <= 0L) return

        consultaCompromisosJob = viewModelScope.launch {
            try {
                val resultado =
                    repositorio.listarCompromisosPorUsuario(idUsuario)

                if (idUsuarioConsultaActual == idUsuario) {
                    _compromisos.value = resultado
                }

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {
                if (idUsuarioConsultaActual == idUsuario) {
                    _compromisos.value = emptyList()
                    _mensaje.value =
                        "No se pudieron cargar los compromisos del usuario."
                }
            } finally {
                if (idUsuarioConsultaActual == idUsuario) {
                    _cargandoCompromisos.value = false
                }
            }
        }
    }



    fun anularCompromiso(idCompromiso: Long) {
        if (_guardando.value) return

        if (idCompromiso <= 0L) {
            _mensaje.value = "Compromiso no válido."
            return
        }

        viewModelScope.launch {
            _guardando.value = true
            _mensaje.value = null

            try {
                repositorio.anularCompromiso(idCompromiso)

                _compromisos.value =
                    _compromisos.value.filterNot {
                        it.idCompromiso == idCompromiso
                    }

                _mensaje.value =
                    "Compromiso anulado correctamente."

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _mensaje.value =
                    "No se pudo anular el compromiso."
            } finally {
                _guardando.value = false
            }
        }
    }


    fun actualizarUsuario(
        idUsuario: Long,
        nombres: String,
        apellidos: String,
        correo: String,
        comuna: String
    ) {
        if (_guardando.value) return

        if (
            idUsuario <= 0L ||
            nombres.isBlank() ||
            apellidos.isBlank() ||
            correo.isBlank() ||
            comuna.isBlank()
        ) {
            _mensaje.value = "Completa todos los campos."
            return
        }

        viewModelScope.launch {
            _guardando.value = true
            _mensaje.value = null

            try {
                val usuarioActualizado =
                    repositorio.actualizarUsuario(
                        idUsuario = idUsuario,
                        datos = ActualizarUsuarioRequest(
                            nombres = nombres.trim(),
                            apellidos = apellidos.trim(),
                            correo = correo.trim(),
                            comuna = comuna.trim()
                        )
                    )

                _usuarios.value = _usuarios.value.map {
                    if (it.idUsuario == idUsuario) {
                        usuarioActualizado
                    } else {
                        it
                    }
                }

                _mensaje.value =
                    "Usuario actualizado correctamente."

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _mensaje.value =
                    "No se pudo actualizar el usuario."
            } finally {
                _guardando.value = false
            }
        }
    }


    fun crearEmpresa(
        nombre: String,
        categoria: String
    ) {
        if (_guardando.value) return

        if (nombre.isBlank() || categoria.isBlank()) {
            _mensaje.value = "Completa todos los campos."
            return
        }

        viewModelScope.launch {
            _guardando.value = true
            _mensaje.value = null

            try {
                val empresaCreada = repositorio.crearEmpresa(
                    nombre = nombre.trim(),
                    categoria = categoria.trim()
                )

                _empresas.value =
                    _empresas.value.filterNot {
                        it.idEmpresa == empresaCreada.idEmpresa
                    } + empresaCreada

                _mensaje.value =
                    "Empresa registrada correctamente."

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _mensaje.value =
                    "No se pudo registrar la empresa."
            } finally {
                _guardando.value = false
            }
        }
    }


    fun actualizarEmpresa(
        idEmpresa: Long,
        nombre: String,
        categoria: String
    ) {
        if (_guardando.value) return

        if (
            idEmpresa <= 0L ||
            nombre.isBlank() ||
            categoria.isBlank()
        ) {
            _mensaje.value = "Completa todos los campos."
            return
        }

        viewModelScope.launch {
            _guardando.value = true
            _mensaje.value = null

            try {
                val empresaActualizada =
                    repositorio.actualizarEmpresa(
                        idEmpresa = idEmpresa,
                        nombre = nombre.trim(),
                        categoria = categoria.trim()
                    )

                _empresas.value = _empresas.value.map {
                    if (it.idEmpresa == idEmpresa) {
                        empresaActualizada
                    } else {
                        it
                    }
                }

                _mensaje.value =
                    "Empresa actualizada correctamente."

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _mensaje.value =
                    "No se pudo actualizar la empresa."
            } finally {
                _guardando.value = false
            }
        }
    }


    fun anularEmpresa(idEmpresa: Long) {
        if (_guardando.value) return

        if (idEmpresa <= 0L) {
            _mensaje.value = "Empresa no válida."
            return
        }

        viewModelScope.launch {
            _guardando.value = true
            _mensaje.value = null

            try {
                repositorio.anularEmpresa(idEmpresa)

                _empresas.value = _empresas.value.filterNot {
                    it.idEmpresa == idEmpresa
                }

                _mensaje.value =
                    "Empresa anulada correctamente."

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _mensaje.value =
                    "No se pudo anular la empresa."
            } finally {
                _guardando.value = false
            }
        }
    }


    fun desactivarUsuario(idUsuario: Long) {
        if (_guardando.value) return

        if (idUsuario <= 0L) {
            _mensaje.value = "Usuario no válido."
            return
        }

        viewModelScope.launch {
            _guardando.value = true
            _mensaje.value = null

            try {
                repositorio.desactivarUsuario(idUsuario)

                _usuarios.value = _usuarios.value.filterNot {
                    it.idUsuario == idUsuario
                }

                _mensaje.value =
                    "Usuario desactivado correctamente."

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _mensaje.value =
                    "No se pudo desactivar el usuario."
            } finally {
                _guardando.value = false
            }
        }
    }


    fun registrarUsuario(
        rut: String,
        nombres: String,
        apellidos: String,
        correo: String,
        contrasena: String,
        comuna: String
    ) {
        if (_guardando.value) return

        if (
            rut.isBlank() ||
            nombres.isBlank() ||
            apellidos.isBlank() ||
            correo.isBlank() ||
            contrasena.isBlank() ||
            comuna.isBlank()
        ) {
            _mensaje.value = "Completa todos los campos."
            return
        }

        viewModelScope.launch {
            _guardando.value = true
            _mensaje.value = null

            try {
                val usuarioCreado = repositorio.registrarUsuario(
                    RegistroUsuarioRequest(
                        rut = rut.trim(),
                        nombres = nombres.trim(),
                        apellidos = apellidos.trim(),
                        correo = correo.trim(),
                        contrasena = contrasena,
                        comuna = comuna.trim()
                    )
                )

                _usuarios.value =
                    _usuarios.value.filterNot {
                        it.idUsuario == usuarioCreado.idUsuario
                    } + usuarioCreado

                _mensaje.value =
                    "Usuario registrado correctamente."

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _mensaje.value =
                    "No se pudo registrar el usuario."
            } finally {
                _guardando.value = false
            }
        }
    }



    fun limpiarMensaje() {
        _mensaje.value = null
    }
}
