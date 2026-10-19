package cl.fintech.pagoflex.model

import java.time.LocalDate

/** Estados posibles de un compromiso de pago. [etiqueta] es el texto que ve la persona. */
enum class EstadoCompromiso(val etiqueta: String) {
    PENDIENTE("Pendiente"),
    VENCIDO("Vencido"),
    PAGADO("Pagado"),
    EN_REVISION("En revisión"),
    ANULADO("Anulado")
}

/**
 * Monto que una persona debe pagar a una empresa (una fila de la tabla "compromisos").
 *
 * [numeroCuota] y [totalCuotas] son nulos cuando el compromiso no es en cuotas.
 * El recargo, los dias de atraso y el total a pagar no se guardan: se calculan.
 */
data class Compromiso(
    val folio: String,
    val idUsuario: Long,
    val codigoEmpresa: String,
    val concepto: String,
    val numeroCuota: Int?,
    val totalCuotas: Int?,
    val monto: Int,
    val fechaVencimiento: LocalDate,
    val estado: EstadoCompromiso
)
