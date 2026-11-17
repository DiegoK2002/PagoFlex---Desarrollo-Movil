package cl.fintech.pagoflex.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.fintech.pagoflex.data.remote.PagoRemoto
import cl.fintech.pagoflex.data.remote.PagosRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.math.BigDecimal
import retrofit2.HttpException

class PagosViewModel : ViewModel() {

    private val repositorio = PagosRepository()

    private var idUsuario: Long = 0L
    private var trabajoCarga: Job? = null
    private var versionCarga: Long = 0L
    private var trabajoRegistro: Job? = null
    private var trabajoComprobante: Job? = null

    private val _comprobante = MutableStateFlow<PagoRemoto?>(null)
    val comprobante: StateFlow<PagoRemoto?> = _comprobante.asStateFlow()

    private val _cargandoComprobante = MutableStateFlow(false)
    val cargandoComprobante: StateFlow<Boolean> = _cargandoComprobante.asStateFlow()

    private val _errorComprobante = MutableStateFlow<String?>(null)
    val errorComprobante: StateFlow<String?> = _errorComprobante.asStateFlow()

    private val _registrando = MutableStateFlow(false)
    val registrando: StateFlow<Boolean> = _registrando.asStateFlow()

    private val _errorRegistro = MutableStateFlow<String?>(null)
    val errorRegistro: StateFlow<String?> = _errorRegistro.asStateFlow()

    private val _pagos =
        MutableStateFlow<List<PagoRemoto>>(emptyList())

    val pagos: StateFlow<List<PagoRemoto>> =
        _pagos.asStateFlow()

    private val _cargando = MutableStateFlow(false)

    val cargando: StateFlow<Boolean> =
        _cargando.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)

    val error: StateFlow<String?> =
        _error.asStateFlow()

    fun cargar(idUsuario: Long) {
        if (idUsuario <= 0L) {
            limpiar()
            return
        }

        this.idUsuario = idUsuario
        actualizar()
    }

    fun actualizar() {
        if (idUsuario <= 0L) return

        trabajoCarga?.cancel()
        versionCarga++

        val versionActual = versionCarga
        val usuarioSolicitado = idUsuario

        _pagos.value = emptyList()
        _cargando.value = true
        _error.value = null

        trabajoCarga = viewModelScope.launch {
            try {
                val resultado = repositorio
                    .listarPorUsuario(usuarioSolicitado)

                if (
                    idUsuario == usuarioSolicitado &&
                    versionCarga == versionActual
                ) {
                    _pagos.value = resultado
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                if (versionCarga == versionActual) {
                    _pagos.value = emptyList()
                    _error.value =
                        "No se pudo cargar el historial de pagos."
                }
            } finally {
                if (versionCarga == versionActual) {
                    _cargando.value = false
                }
            }
        }
    }

    fun cargarComprobante(idUsuario: Long, idCompromiso: Long) {
        if (
            _comprobante.value?.idUsuario == idUsuario &&
            _comprobante.value?.idCompromiso == idCompromiso
        ) return

        trabajoComprobante?.cancel()
        _comprobante.value = null
        _errorComprobante.value = null
        _cargandoComprobante.value = true

        trabajoComprobante = viewModelScope.launch {
            try {
                val resultado = repositorio.buscarComprobante(idUsuario, idCompromiso)
                _comprobante.value = resultado
                if (resultado == null) {
                    _errorComprobante.value = "No se encontró el pago de este compromiso."
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _errorComprobante.value = "No se pudo consultar el comprobante."
            } finally {
                _cargandoComprobante.value = false
            }
        }
    }

    fun limpiarEstadoRegistro() {
        _errorRegistro.value = null
    }

    fun registrarPago(
        idUsuario: Long,
        idCompromiso: Long,
        monto: BigDecimal,
        onRegistrado: (PagoRemoto) -> Unit
    ) {
        if (_registrando.value) return

        if (idUsuario <= 0L || idCompromiso <= 0L || monto <= BigDecimal.ZERO) {
            _errorRegistro.value = "Los datos del pago no son válidos."
            return
        }

        _errorRegistro.value = null
        _registrando.value = true

        trabajoRegistro = viewModelScope.launch {
            try {
                val pago = repositorio.registrarPago(
                    idUsuario = idUsuario,
                    idCompromiso = idCompromiso,
                    monto = monto,
                    metodoPago = "TRANSFERENCIA"
                )

                if (
                    !pago.activo ||
                    pago.idUsuario != idUsuario ||
                    pago.idCompromiso != idCompromiso
                ) {
                    _errorRegistro.value = "El servidor devolvió un pago no válido."
                } else {
                    trabajoComprobante?.cancel()
                    _comprobante.value = pago
                    _errorComprobante.value = null
                    _cargandoComprobante.value = false
                    onRegistrado(pago)
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: HttpException) {
                _errorRegistro.value = when (error.code()) {
                    400 -> "No se pudo registrar el pago. Verifica el compromiso y su monto."
                    404 -> "El compromiso no está disponible en el servidor."
                    409 -> "Ya existe un pago activo para este compromiso. Revisa el historial."
                    else -> "No se pudo registrar el pago (HTTP ${error.code()})."
                }
            } catch (error: IOException) {
                _errorRegistro.value =
                    "No se pudo confirmar el resultado. Revisa el historial antes de intentarlo otra vez."
            } catch (error: Exception) {
                _errorRegistro.value = "Ocurrió un error al registrar el pago."
            } finally {
                _registrando.value = false
                trabajoRegistro = null
            }
        }
    }

    fun limpiar() {
        trabajoComprobante?.cancel()
        trabajoComprobante = null
        _comprobante.value = null
        _cargandoComprobante.value = false
        _errorComprobante.value = null
        trabajoRegistro?.cancel()
        trabajoRegistro = null
        _registrando.value = false
        _errorRegistro.value = null
        trabajoCarga?.cancel()
        trabajoCarga = null
        versionCarga++
        idUsuario = 0L
        _pagos.value = emptyList()
        _cargando.value = false
        _error.value = null
    }
}
