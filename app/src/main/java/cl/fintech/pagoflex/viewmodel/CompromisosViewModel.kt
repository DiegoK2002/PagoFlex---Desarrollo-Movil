package cl.fintech.pagoflex.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.fintech.pagoflex.data.remote.ApiClient
import cl.fintech.pagoflex.data.remote.CompromisosRepository
import cl.fintech.pagoflex.data.remote.aCompromiso
import cl.fintech.pagoflex.model.Compromiso
import cl.fintech.pagoflex.model.EstadoCompromiso
import cl.fintech.pagoflex.model.MotivoNoPago
import cl.fintech.pagoflex.model.Pago
import cl.fintech.pagoflex.util.IndicadorCumplimiento
import cl.fintech.pagoflex.util.Reconocimiento
import cl.fintech.pagoflex.util.ResumenCompromisos
import cl.fintech.pagoflex.util.calcularDiasDeAtraso
import cl.fintech.pagoflex.util.calcularResumenCompromisos
import cl.fintech.pagoflex.util.compromisoAnterior
import cl.fintech.pagoflex.util.motivoNoPagable
import java.time.LocalDate
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CompromisosViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repositorio = CompromisosRepository()
    private val empresasApi = ApiClient.empresasApi
    private var nombresEmpresas: Map<String, String> = emptyMap()

    private var idUsuario: Long = 0L

    private var trabajoCarga: Job? = null

    private val hoy: LocalDate
        get() = LocalDate.now()

    private val _compromisos =
        MutableStateFlow<List<Compromiso>>(emptyList())

    val compromisos: StateFlow<List<Compromiso>> =
        _compromisos.asStateFlow()

    private val _resumenCompromisos = MutableStateFlow(
        ResumenCompromisos(
            totalPorPagarMes = 0,
            proximoVencimiento = null,
            cantidadVencidos = 0
        )
    )

    val resumenCompromisos: StateFlow<ResumenCompromisos> =
        _resumenCompromisos.asStateFlow()

    private val _pagos =
        MutableStateFlow<List<Pago>>(emptyList())

    val pagos: StateFlow<List<Pago>> =
        _pagos.asStateFlow()

    private val _indicadorCumplimiento =
        MutableStateFlow(IndicadorCumplimiento(0, 0))

    val indicadorCumplimiento: StateFlow<IndicadorCumplimiento> =
        _indicadorCumplimiento.asStateFlow()

    private val _reconocimientos =
        MutableStateFlow<List<Reconocimiento>>(emptyList())

    val reconocimientos: StateFlow<List<Reconocimiento>> =
        _reconocimientos.asStateFlow()

    private val _cargando = MutableStateFlow(false)

    val cargando: StateFlow<Boolean> =
        _cargando.asStateFlow()

    private val _errorCarga = MutableStateFlow<String?>(null)

    val errorCarga: StateFlow<String?> =
        _errorCarga.asStateFlow()

    fun cargar(idUsuario: Long) {
        if (idUsuario <= 0L) {
            limpiar()
            return
        }

        this.idUsuario = idUsuario
        recargar()
    }

    private fun reiniciarDatos() {
        _compromisos.value = emptyList()
        _pagos.value = emptyList()
        _indicadorCumplimiento.value =
            IndicadorCumplimiento(0, 0)
        _reconocimientos.value = emptyList()
        _resumenCompromisos.value = ResumenCompromisos(
            totalPorPagarMes = 0,
            proximoVencimiento = null,
            cantidadVencidos = 0
        )
    }

    fun limpiar() {
        trabajoCarga?.cancel()
        trabajoCarga = null
        idUsuario = 0L
        nombresEmpresas = emptyMap()
        reiniciarDatos()
        _cargando.value = false
        _errorCarga.value = null
    }

    private fun recargar() {
        trabajoCarga?.cancel()
        reiniciarDatos()
        _cargando.value = true
        _errorCarga.value = null

        val usuarioSolicitado = idUsuario

        trabajoCarga = viewModelScope.launch {
            try {
                val resultado = repositorio
                    .listarPorUsuario(usuarioSolicitado)
                    .map { it.aCompromiso() }

                val empresas = try {
                    empresasApi.listarTodas()
                        .filter { it.idEmpresa > 0L && it.nombre.isNotBlank() }
                        .associate { it.idEmpresa.toString() to it.nombre.trim() }
                } catch (error: CancellationException) {
                    throw error
                } catch (error: Exception) {
                    emptyMap()
                }

                if (idUsuario != usuarioSolicitado) {
                    return@launch
                }

                nombresEmpresas = empresas
                _compromisos.value = resultado

                _resumenCompromisos.value =
                    calcularResumenCompromisos(
                        compromisos = resultado,
                        importeDe = ::importeDe
                    )

            } catch (error: CancellationException) {
                throw error

            } catch (error: Exception) {
                if (idUsuario == usuarioSolicitado) {
                    reiniciarDatos()
                    _errorCarga.value =
                        "No se pudieron cargar los compromisos desde el servidor."
                }

            } finally {
                if (idUsuario == usuarioSolicitado) {
                    _cargando.value = false
                }
            }
        }
    }

    fun actualizar() {
        if (idUsuario > 0L) {
            recargar()
        }
    }

    fun nombreEmpresa(codigo: String): String =
        nombresEmpresas[codigo] ?: "Empresa $codigo"

    fun diasDeAtraso(compromiso: Compromiso): Int =
        calcularDiasDeAtraso(compromiso, hoy)

    fun recargoDe(compromiso: Compromiso): Int = 0

    fun importeDe(compromiso: Compromiso): Int =
        compromiso.monto + recargoDe(compromiso)

    fun anteriorPendiente(
        compromiso: Compromiso
    ): Compromiso? =
        compromisoAnterior(
            compromiso,
            _compromisos.value
        )

    fun motivoDe(
        compromiso: Compromiso
    ): MotivoNoPago? =
        motivoNoPagable(
            compromiso,
            anteriorPendiente(compromiso)
        )

    fun pagar(compromiso: Compromiso): Pago? = null

    fun pagoDe(folio: String): Pago? = null
}
