package cl.fintech.pagoflex.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.fintech.pagoflex.data.remote.ReportesRepository
import cl.fintech.pagoflex.model.Reporte
import cl.fintech.pagoflex.model.Rol
import cl.fintech.pagoflex.model.Usuario
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReportesViewModel : ViewModel() {

    private val repositorio = ReportesRepository()
    private var usuarioActual: Usuario? = null
    private var trabajoCarga: Job? = null
    private var trabajoOperacion: Job? = null
    private var versionCarga = 0L

    private val _reportes = MutableStateFlow<List<Reporte>>(emptyList())
    val reportes: StateFlow<List<Reporte>> = _reportes.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _operando = MutableStateFlow(false)
    val operando: StateFlow<Boolean> = _operando.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun cargar(usuario: Usuario) {
        trabajoCarga?.cancel()
        trabajoOperacion?.cancel()
        usuarioActual = usuario
        _reportes.value = emptyList()
        _operando.value = false
        _error.value = null
        actualizar()
    }

    fun actualizar() {
        trabajoCarga?.cancel()
        versionCarga++
        val version = versionCarga
        val usuario = usuarioActual

        if (usuario == null) {
            _reportes.value = emptyList()
            _cargando.value = false
            return
        }

        _cargando.value = true
        _error.value = null
        trabajoCarga = viewModelScope.launch {
            try {
                val resultado = when (usuario.rol) {
                    Rol.AGENTE -> repositorio.listarParaAgente()
                    Rol.USUARIO_FINAL -> repositorio.listarPorUsuario(usuario.id)
                }
                if (versionCarga == version && usuarioActual?.id == usuario.id) {
                    _reportes.value = resultado
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (versionCarga == version) {
                    _reportes.value = emptyList()
                    _error.value = "No se pudieron cargar los reportes."
                }
            } finally {
                if (versionCarga == version) {
                    _cargando.value = false
                }
            }
        }
    }

    fun crearReporte(
        folio: String,
        motivo: String,
        comentario: String,
        onResultado: (Boolean) -> Unit
    ) {
        val usuario = usuarioActual
        if (usuario == null || usuario.rol != Rol.USUARIO_FINAL || _operando.value) {
            onResultado(false)
            return
        }

        _operando.value = true
        _error.value = null
        trabajoOperacion = viewModelScope.launch {
            try {
                repositorio.registrar(usuario.id, folio, motivo, comentario)
                if (usuarioActual?.id == usuario.id) {
                    actualizar()
                    onResultado(true)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (usuarioActual?.id == usuario.id) {
                    _error.value = "No se pudo registrar el reporte."
                    onResultado(false)
                }
            } finally {
                if (usuarioActual?.id == usuario.id) {
                    _operando.value = false
                }
            }
        }
    }

    fun tomarReporte(idReporte: Long) {
        val usuario = usuarioActual
        if (usuario == null || usuario.rol != Rol.AGENTE || _operando.value) return

        _operando.value = true
        _error.value = null
        trabajoOperacion = viewModelScope.launch {
            try {
                repositorio.tomarReporte(idReporte, usuario.id)
                if (usuarioActual?.id == usuario.id) actualizar()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (usuarioActual?.id == usuario.id) {
                    _error.value = "No se pudo tomar el reporte."
                }
            } finally {
                if (usuarioActual?.id == usuario.id) _operando.value = false
            }
        }
    }

    fun resolverReporte(
        idReporte: Long,
        respuesta: String,
        onResultado: (Boolean) -> Unit
    ) {
        val usuario = usuarioActual
        if (
            usuario == null || usuario.rol != Rol.AGENTE ||
            respuesta.isBlank() || _operando.value
        ) {
            onResultado(false)
            return
        }

        _operando.value = true
        _error.value = null
        trabajoOperacion = viewModelScope.launch {
            try {
                repositorio.resolverReporte(idReporte, respuesta)
                if (usuarioActual?.id == usuario.id) {
                    actualizar()
                    onResultado(true)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (usuarioActual?.id == usuario.id) {
                    _error.value = "No se pudo resolver el reporte."
                    onResultado(false)
                }
            } finally {
                if (usuarioActual?.id == usuario.id) _operando.value = false
            }
        }
    }

    fun limpiar() {
        trabajoCarga?.cancel()
        trabajoOperacion?.cancel()
        versionCarga++
        usuarioActual = null
        _reportes.value = emptyList()
        _cargando.value = false
        _operando.value = false
        _error.value = null
    }
}
