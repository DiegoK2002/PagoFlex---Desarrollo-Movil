package cl.app.pagoflex.viewmodel

import androidx.lifecycle.ViewModel
import cl.app.pagoflex.data.DatosEjemplo
import cl.app.pagoflex.data.RepositorioPagoFlex
import cl.app.pagoflex.model.Compromiso
import cl.app.pagoflex.model.EstadoCompromiso
import cl.app.pagoflex.model.MotivoNoPago
import cl.app.pagoflex.model.Pago
import cl.app.pagoflex.util.calcularDiasDeAtraso
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDateTime
import java.time.LocalTime

// =====================================================================
//  CompromisosViewModel.kt  —  VIEWMODEL COMPARTIDO DE COMPROMISOS Y PAGOS
//  ---------------------------------------------------------------------
//  Igual que UsuariosViewModel de MiRegistro: se crea en AppNavegacion
//  (fuera del NavHost) y lo comparten la lista, el detalle y el comprobante.
//  Aquí vive la LÓGICA del pago (la vista no decide si se puede pagar):
//    - RN-03: el recargo solo se suma si hay atraso.
//    - RN-04: se paga primero el vencimiento más antiguo de cada empresa.
//    - RN-06: pago total, sin abonos.
//    - RN-09: un compromiso En revisión o Anulado no se paga.
//    - RN-05: el comprobante no se modifica ni se elimina.
//  Los datos viven en MEMORIA: si se cierra la app, se reinician.
//  "hoy" es la fecha de referencia de los datos de ejemplo (06-10-2026).
//  Regla: el ViewModel NUNCA importa nada de Compose.
// =====================================================================

class CompromisosViewModel : ViewModel() {

    private val hoy = DatosEjemplo.FECHA_REFERENCIA

    private val _compromisos = MutableStateFlow(DatosEjemplo.compromisos)
    val compromisos: StateFlow<List<Compromiso>> = _compromisos.asStateFlow()

    private val _pagos = MutableStateFlow(DatosEjemplo.pagos)
    val pagos: StateFlow<List<Pago>> = _pagos.asStateFlow()

    // ----- Cálculos (no se guardan) -----

    fun diasDeAtraso(compromiso: Compromiso): Int = calcularDiasDeAtraso(compromiso, hoy)

    fun recargoDe(compromiso: Compromiso): Int =
        if (diasDeAtraso(compromiso) > 0) RepositorioPagoFlex.recargoDe(compromiso.codigoEmpresa) else 0

    fun totalAPagar(compromiso: Compromiso): Int = compromiso.monto + recargoDe(compromiso)

    // ----- Reglas de pago -----

    /** El compromiso más antiguo, de la misma persona y empresa, que aún está sin pagar (RN-04). */
    fun compromisoAnterior(compromiso: Compromiso): Compromiso? =
        _compromisos.value
            .filter {
                it.codigoUsuario == compromiso.codigoUsuario &&
                    it.codigoEmpresa == compromiso.codigoEmpresa &&
                    it.fechaVencimiento < compromiso.fechaVencimiento &&
                    (it.estado == EstadoCompromiso.PENDIENTE || it.estado == EstadoCompromiso.VENCIDO)
            }
            .minByOrNull { it.fechaVencimiento }

    /** null = se puede pagar. Si no, devuelve el motivo para explicárselo a la persona. */
    fun motivoNoPagable(compromiso: Compromiso): MotivoNoPago? = when {
        compromiso.estado == EstadoCompromiso.PAGADO -> MotivoNoPago.YA_PAGADO
        compromiso.estado == EstadoCompromiso.EN_REVISION -> MotivoNoPago.EN_REVISION
        compromiso.estado == EstadoCompromiso.ANULADO -> MotivoNoPago.ANULADO
        compromisoAnterior(compromiso) != null -> MotivoNoPago.HAY_ANTERIOR
        else -> null
    }

    /**
     * Confirma el pago (simulado): crea el comprobante y deja el compromiso Pagado.
     * Devuelve el comprobante, o null si el compromiso no se puede pagar.
     */
    fun pagar(compromiso: Compromiso): Pago? {
        if (motivoNoPagable(compromiso) != null) return null

        val siguiente = _pagos.value.maxOf { it.numeroComprobante.removePrefix("CPR-").toInt() } + 1
        val cuota = compromiso.numeroCuota
        val total = compromiso.totalCuotas
        val concepto = if (cuota != null && total != null) {
            "${compromiso.concepto} $cuota/$total"
        } else {
            compromiso.concepto
        }

        val pago = Pago(
            numeroComprobante = "CPR-" + siguiente.toString().padStart(6, '0'),
            folioCompromiso = compromiso.folio,
            codigoUsuario = compromiso.codigoUsuario,
            codigoEmpresa = compromiso.codigoEmpresa,
            concepto = concepto,
            fechaHora = LocalDateTime.of(hoy, LocalTime.now()),
            montoPagado = totalAPagar(compromiso),
            canal = "App",
            aTiempo = !hoy.isAfter(compromiso.fechaVencimiento)    // RN-10
        )

        _pagos.update { lista -> lista + pago }
        _compromisos.update { lista ->
            lista.map { if (it.folio == compromiso.folio) it.copy(estado = EstadoCompromiso.PAGADO) else it }
        }
        return pago
    }
}
