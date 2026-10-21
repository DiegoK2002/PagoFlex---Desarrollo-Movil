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

fun colorDeEstado(estado: EstadoCompromiso): Color = when (estado) {
    EstadoCompromiso.PENDIENTE -> ColoresEstado.Pendiente
    EstadoCompromiso.VENCIDO -> ColoresEstado.Vencido
    EstadoCompromiso.PAGADO -> ColoresEstado.Pagado
    EstadoCompromiso.EN_REVISION -> ColoresEstado.EnRevision
    EstadoCompromiso.ANULADO -> ColoresEstado.Anulado
}

fun textoConcepto(compromiso: Compromiso): String {
    val cuota = compromiso.numeroCuota
    val total = compromiso.totalCuotas
    return if (cuota != null && total != null) {
        "${compromiso.concepto} · cuota $cuota/$total"
    } else {
        compromiso.concepto
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TarjetaCompromiso(
    compromiso: Compromiso,
    nombreEmpresa: String,
    importe: Int,
    modifier: Modifier = Modifier,
    importeConvertido: String? = null,
    onClick: () -> Unit = {}
)
 {

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

            Text(
                importeConvertido ?: formatearPesos(importe),
                style = MaterialTheme.typography.titleLarge
            )

            if (importeConvertido != null) {
                Text(
                    "Equivalente aproximado. Importe real: ${formatearPesos(importe)} CLP",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (avisoRecargo != null) {
                Text(avisoRecargo, style = MaterialTheme.typography.bodySmall)
            }
            Text(
                "Vence el ${formatearFecha(compromiso.fechaVencimiento)}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                compromiso.estado.etiqueta,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = colorDeEstado(compromiso.estado)
            )
        }
    }
}
