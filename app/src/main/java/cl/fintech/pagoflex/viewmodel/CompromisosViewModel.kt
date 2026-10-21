package cl.fintech.pagoflex.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import cl.fintech.pagoflex.data.PagoFlexDbHelper
import cl.fintech.pagoflex.model.Compromiso
import cl.fintech.pagoflex.model.EstadoCompromiso
import cl.fintech.pagoflex.model.MotivoNoPago
import cl.fintech.pagoflex.model.Pago
import java.time.LocalDate
import cl.fintech.pagoflex.util.calcularDiasDeAtraso
import cl.fintech.pagoflex.util.compromisoAnterior
import cl.fintech.pagoflex.util.motivoNoPagable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDateTime
import java.time.LocalTime
import cl.fintech.pagoflex.util.IndicadorCumplimiento
import cl.fintech.pagoflex.util.calcularIndicadorCumplimiento
import cl.fintech.pagoflex.util.Reconocimiento
import cl.fintech.pagoflex.util.calcularReconocimientosPorPagos
import cl.fintech.pagoflex.util.cumpleMetaEjemplar
import cl.fintech.pagoflex.util.ResumenCompromisos
import cl.fintech.pagoflex.util.calcularResumenCompromisos

class CompromisosViewModel(application: Application) : AndroidViewModel(application) {

    private val bd = PagoFlexDbHelper.obtener(application)

    private val hoy: LocalDate
        get() = LocalDate.now()


    private val _compromisos = MutableStateFlow<List<Compromiso>>(emptyList())
    val compromisos: StateFlow<List<Compromiso>> = _compromisos.asStateFlow()

    private val _resumenCompromisos = MutableStateFlow(
        ResumenCompromisos(
            totalPorPagarMes = 0,
            proximoVencimiento = null,
            cantidadVencidos = 0
        )
    )

    val resumenCompromisos: StateFlow<ResumenCompromisos> =
        _resumenCompromisos.asStateFlow()

    private val _pagos = MutableStateFlow<List<Pago>>(emptyList())
    val pagos: StateFlow<List<Pago>> = _pagos.asStateFlow()

    private val _indicadorCumplimiento = MutableStateFlow(
        IndicadorCumplimiento(0, 0)
    )

    val indicadorCumplimiento: StateFlow<IndicadorCumplimiento> =
        _indicadorCumplimiento.asStateFlow()


    private val _reconocimientos = MutableStateFlow<List<Reconocimiento>>(
        emptyList()
    )

    val reconocimientos: StateFlow<List<Reconocimiento>> =
        _reconocimientos.asStateFlow()

    private var montosPagados: Map<String, Int> = emptyMap()

    private var idUsuario: Long = 0

    fun cargar(idUsuario: Long) {
        this.idUsuario = idUsuario
        recargar()
    }

    fun limpiar() {
        idUsuario = 0
        montosPagados = emptyMap()
        _compromisos.value = emptyList()
        _pagos.value = emptyList()
        _indicadorCumplimiento.value = IndicadorCumplimiento(0, 0)
        _reconocimientos.value = emptyList()

        _resumenCompromisos.value = ResumenCompromisos(
            totalPorPagarMes = 0,
            proximoVencimiento = null,
            cantidadVencidos = 0
        )

    }


    private fun recargar() {
        montosPagados = bd.montosPagadosDeUsuario(idUsuario)
        _compromisos.value = bd.compromisosDeUsuario(idUsuario)
        _pagos.value = bd.pagosDeUsuario(idUsuario)

        _resumenCompromisos.value = calcularResumenCompromisos(
            compromisos = _compromisos.value,
            importeDe = ::importeDe
        )

        _indicadorCumplimiento.value = calcularIndicadorCumplimiento(
            compromisos = _compromisos.value,
            pagos = _pagos.value
        )

        if (cumpleMetaEjemplar(_indicadorCumplimiento.value)) {
            bd.registrarReconocimientoEjemplar(
                idUsuario = idUsuario,
                fecha = LocalDate.now()
            )
        }

        _reconocimientos.value = calcularReconocimientosPorPagos(
            _pagos.value
        ) + Reconocimiento(
            titulo = "Cumplimiento ejemplar",
            descripcion = "Alcanzaste al menos un 95 % de cumplimiento en tus compromisos evaluables.",
            fechaObtenida = bd.fechaReconocimientoEjemplar(idUsuario),
            faltantes = 0
        )
    }

    fun actualizar() {
        if (idUsuario > 0L) {
            recargar()
        }
    }

    fun nombreEmpresa(codigo: String): String = bd.nombreEmpresa(codigo)

    fun diasDeAtraso(compromiso: Compromiso): Int = calcularDiasDeAtraso(compromiso, hoy)

    fun recargoDe(compromiso: Compromiso): Int = when {
        compromiso.estado == EstadoCompromiso.PAGADO ->
            ((montosPagados[compromiso.folio] ?: compromiso.monto) - compromiso.monto).coerceAtLeast(0)
        diasDeAtraso(compromiso) > 0 -> bd.recargoDe(compromiso.codigoEmpresa)
        else -> 0
    }

    fun importeDe(compromiso: Compromiso): Int = compromiso.monto + recargoDe(compromiso)

    fun anteriorPendiente(compromiso: Compromiso): Compromiso? =
        compromisoAnterior(compromiso, _compromisos.value)

    fun motivoDe(compromiso: Compromiso): MotivoNoPago? =
        motivoNoPagable(compromiso, anteriorPendiente(compromiso))

    fun pagar(compromiso: Compromiso): Pago? {
        if (idUsuario <= 0L || compromiso.idUsuario != idUsuario) {
            return null
        }

        val actual = _compromisos.value.firstOrNull {
            it.folio == compromiso.folio &&
                    it.idUsuario == idUsuario
        } ?: return null

        if (motivoDe(actual) != null) return null

        val cuota = actual.numeroCuota
        val total = actual.totalCuotas
        val concepto = if (cuota != null && total != null) {
            "${actual.concepto} $cuota/$total"
        } else {
            actual.concepto
        }

        val pago = Pago(
            numeroComprobante = "CPR-" + bd.siguienteNumeroComprobante().toString().padStart(6, '0'),
            folioCompromiso = actual.folio,
            idUsuario = actual.idUsuario,
            codigoEmpresa = actual.codigoEmpresa,
            concepto = concepto,
            fechaHora = LocalDateTime.of(hoy, LocalTime.now()),
            montoPagado = importeDe(actual),
            canal = "App",
            aTiempo = !hoy.isAfter(actual.fechaVencimiento)
        )

        return if (bd.registrarPago(pago)) {
            recargar()
            pago
        } else {
            null
        }
    }

    fun pagoDe(folio: String): Pago? {
        if (idUsuario <= 0L || folio.isBlank()) return null

        return bd.pagoPorFolioDeUsuario(
            folio = folio,
            idUsuario = idUsuario
        )
    }


}
