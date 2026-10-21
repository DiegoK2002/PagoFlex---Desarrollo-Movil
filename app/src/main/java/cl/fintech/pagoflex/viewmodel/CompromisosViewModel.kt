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

/**
 * Compromisos y pagos de la persona con sesion iniciada, leidos desde SQLite. Lo comparten la
 * lista, el detalle y el comprobante. Aqui vive la logica del pago:
 *  - el recargo solo se suma si el compromiso esta vencido;
 *  - los compromisos de una misma empresa se pagan en orden de vencimiento;
 *  - el pago es por el total, sin abonos;
 *  - no se pagan compromisos en revision, anulados ni ya pagados.
 * Cada persona ve solo sus compromisos: la consulta filtra por su id.
 */
class CompromisosViewModel(application: Application) : AndroidViewModel(application) {

    private val bd = PagoFlexDbHelper.obtener(application)

    // Fecha que se usa como "hoy" para calcular los dias de atraso

    private val hoy: LocalDate
        get() = LocalDate.now()


    private val _compromisos = MutableStateFlow<List<Compromiso>>(emptyList())
    val compromisos: StateFlow<List<Compromiso>> = _compromisos.asStateFlow()

    // Monto realmente pagado por cada compromiso ya pagado, indexado por folio
    private var montosPagados: Map<String, Int> = emptyMap()

    // Id de la persona cuyos datos estan cargados (0 = ninguna)
    private var idUsuario: Long = 0

    /** Carga los compromisos de la persona que inicio sesion. */
    fun cargar(idUsuario: Long) {
        this.idUsuario = idUsuario
        recargar()
    }

    /** Al cerrar sesion no queda nada de la persona anterior en memoria. */
    fun limpiar() {
        idUsuario = 0
        montosPagados = emptyMap()
        _compromisos.value = emptyList()
    }

    /** Vuelve a leer desde la base de datos. Los montos pagados se actualizan antes que la lista. */
    private fun recargar() {
        montosPagados = bd.montosPagadosDeUsuario(idUsuario)
        _compromisos.value = bd.compromisosDeUsuario(idUsuario)
    }

    fun actualizar() {
        if (idUsuario > 0L) {
            recargar()
        }
    }

    fun nombreEmpresa(codigo: String): String = bd.nombreEmpresa(codigo)

    // ------------------------------------------------------------------
    // Calculos: no se guardan, se obtienen cada vez
    // ------------------------------------------------------------------

    /** Dias de atraso del compromiso (0 si no esta vencido). */
    fun diasDeAtraso(compromiso: Compromiso): Int = calcularDiasDeAtraso(compromiso, hoy)

    /**
     * Recargo del compromiso. Si ya se pago, es la diferencia entre lo pagado y el monto original;
     * si esta vencido, es el recargo de la empresa; en cualquier otro caso es 0.
     */
    fun recargoDe(compromiso: Compromiso): Int = when {
        compromiso.estado == EstadoCompromiso.PAGADO ->
            ((montosPagados[compromiso.folio] ?: compromiso.monto) - compromiso.monto).coerceAtLeast(0)
        diasDeAtraso(compromiso) > 0 -> bd.recargoDe(compromiso.codigoEmpresa)
        else -> 0
    }

    /** Importe del compromiso: lo que hay que pagar, o lo que se pago si ya esta pagado. */
    fun importeDe(compromiso: Compromiso): Int = compromiso.monto + recargoDe(compromiso)

    // ------------------------------------------------------------------
    // Reglas de pago
    // ------------------------------------------------------------------

    /** Compromiso anterior sin pagar de la misma empresa, si existe. */
    fun anteriorPendiente(compromiso: Compromiso): Compromiso? =
        compromisoAnterior(compromiso, _compromisos.value)

    /** Motivo por el que no se puede pagar, o null si se puede. */
    fun motivoDe(compromiso: Compromiso): MotivoNoPago? =
        motivoNoPagable(compromiso, anteriorPendiente(compromiso))

    // ------------------------------------------------------------------
    // Pago
    // ------------------------------------------------------------------

    /**
     * Confirma el pago: guarda el comprobante y deja el compromiso como Pagado.
     * Devuelve el comprobante, o null si no se pudo pagar.
     */
    fun pagar(compromiso: Compromiso): Pago? {
        // Se usa el estado actual guardado en memoria, no la copia que recibio la pantalla,
        // que podria estar desactualizada si se toco el boton dos veces
        val actual = _compromisos.value.firstOrNull { it.folio == compromiso.folio } ?: return null
        if (motivoDe(actual) != null) return null

        // Concepto del comprobante, con la cuota cuando corresponde: "Credito de consumo 8/24"
        val cuota = actual.numeroCuota
        val total = actual.totalCuotas
        val concepto = if (cuota != null && total != null) {
            "${actual.concepto} $cuota/$total"
        } else {
            actual.concepto
        }

        val pago = Pago(
            // Numero siguiente al mayor existente, con ceros a la izquierda: CPR-000454
            numeroComprobante = "CPR-" + bd.siguienteNumeroComprobante().toString().padStart(6, '0'),
            folioCompromiso = actual.folio,
            idUsuario = actual.idUsuario,
            codigoEmpresa = actual.codigoEmpresa,
            concepto = concepto,
            fechaHora = LocalDateTime.of(hoy, LocalTime.now()),
            montoPagado = importeDe(actual),                      // monto mas recargo, si corresponde
            canal = "App",
            aTiempo = !hoy.isAfter(actual.fechaVencimiento)       // a tiempo si se paga hasta el dia del vencimiento
        )

        // La base de datos guarda el pago y cambia el estado en una sola transaccion
        return if (bd.registrarPago(pago)) {
            recargar()
            pago
        } else {
            null
        }
    }

    /** Comprobante de un compromiso ya pagado, o null si no tiene. */
    fun pagoDe(folio: String): Pago? = bd.pagoPorFolio(folio)
}
