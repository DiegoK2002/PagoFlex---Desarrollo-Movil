package cl.fintech.pagoflex.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import cl.fintech.pagoflex.model.Compromiso
import cl.fintech.pagoflex.model.EstadoCompromiso
import cl.fintech.pagoflex.ui.styles.EstilosTarjeta
import cl.fintech.pagoflex.ui.theme.ColoresEstado
import cl.fintech.pagoflex.ui.theme.Dimens
import cl.fintech.pagoflex.util.formatearFecha
import cl.fintech.pagoflex.util.formatearPesos

/** Color con que se muestra cada estado de un compromiso. */
fun colorDeEstado(estado: EstadoCompromiso): Color = when (estado) {
    EstadoCompromiso.PENDIENTE -> ColoresEstado.Pendiente
    EstadoCompromiso.VENCIDO -> ColoresEstado.Vencido
    EstadoCompromiso.PAGADO -> ColoresEstado.Pagado
    EstadoCompromiso.EN_REVISION -> ColoresEstado.EnRevision
    EstadoCompromiso.ANULADO -> ColoresEstado.Anulado
}

/** Concepto con su numero de cuota: "Credito de consumo · cuota 8/24". Si no es en cuotas, solo el concepto. */
fun textoConcepto(compromiso: Compromiso): String {
    val cuota = compromiso.numeroCuota
    val total = compromiso.totalCuotas
    return if (cuota != null && total != null) {
        "${compromiso.concepto} · cuota $cuota/$total"
    } else {
        compromiso.concepto
    }
}

/**
 * Tarjeta con el resumen de un compromiso: empresa, concepto, importe, vencimiento y estado.
 * No consulta datos ni navega: dibuja lo que recibe y avisa con [onClick] cuando la tocan.
 *
 * @param importe lo que hay que pagar (monto mas recargo) o, si ya se pago, lo que se pago.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TarjetaCompromiso(
    compromiso: Compromiso,
    nombreEmpresa: String,
    importe: Int,
    importeConvertido: String? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    // Si el importe es mayor que el monto original, se aclara que incluye un recargo
    val avisoRecargo = when {
        importe == compromiso.monto -> null
        compromiso.estado == EstadoCompromiso.PAGADO -> "Se pagó con recargo por atraso"
        else -> "Incluye recargo por atraso"
    }

    Card(
        onClick = onClick,
        shape = EstilosTarjeta.forma,
        colors = EstilosTarjeta.colores(),
        elevation = EstilosTarjeta.elevacion(),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(Dimens.espacioMedio),
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioMini)
        ) {
            Text(nombreEmpresa, style = MaterialTheme.typography.titleMedium)
            Text(textoConcepto(compromiso), style = MaterialTheme.typography.bodyMedium)
            Text(formatearPesos(importe), style = MaterialTheme.typography.titleLarge)
            if (avisoRecargo != null) {
                Text(avisoRecargo, style = MaterialTheme.typography.bodySmall)
            }
            Text(
                "Vence el ${formatearFecha(compromiso.fechaVencimiento)}",
                style = MaterialTheme.typography.bodyMedium
            )
            // Estado con el color que le corresponde
            Text(
                compromiso.estado.etiqueta,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = colorDeEstado(compromiso.estado)
            )
        }
    }
}
