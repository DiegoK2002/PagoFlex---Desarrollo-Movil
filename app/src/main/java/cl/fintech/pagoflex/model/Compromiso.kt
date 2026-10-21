package cl.fintech.pagoflex.model

import java.time.LocalDate

enum class EstadoCompromiso(val etiqueta: String) {
    PENDIENTE("Pendiente"),
    VENCIDO("Vencido"),
    PAGADO("Pagado"),
    EN_REVISION("En revisión"),
    ANULADO("Anulado")
}

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
