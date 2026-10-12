package cl.fintech.pagoflex.model

import java.time.LocalDate

// =====================================================================
//  Compromiso.kt  —  LO QUE UNA PERSONA DEBE PAGAR A UNA EMPRESA
//  ---------------------------------------------------------------------
//  Estados: Pendiente, Vencido, Pagado, En revisión, Anulado.
//  Lo que se calcula y no se guarda: total a pagar, días de atraso,
//  si el pago fue a tiempo.
// =====================================================================

enum class EstadoCompromiso(val etiqueta: String) {
    PENDIENTE("Pendiente"),
    VENCIDO("Vencido"),
    PAGADO("Pagado"),
    EN_REVISION("En revisión"),
    ANULADO("Anulado")
}

data class Compromiso(
    val folio: String,
    val codigoUsuario: String,
    val codigoEmpresa: String,
    val concepto: String,
    val numeroCuota: Int?,        // null si no es en cuotas
    val totalCuotas: Int?,
    val monto: Int,               // pesos chilenos
    val fechaVencimiento: LocalDate,
    val estado: EstadoCompromiso
)
