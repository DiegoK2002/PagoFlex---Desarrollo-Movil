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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import cl.fintech.pagoflex.R
import cl.fintech.pagoflex.model.Compromiso
import cl.fintech.pagoflex.model.EstadoCompromiso
import cl.fintech.pagoflex.ui.styles.EstilosTarjeta
import cl.fintech.pagoflex.ui.theme.Dimens
import cl.fintech.pagoflex.ui.theme.ColoresEstado
import cl.fintech.pagoflex.util.formatearFecha
import cl.fintech.pagoflex.util.formatearPesos

// =====================================================================
//  TarjetaCompromiso.kt  —  COMPONENTE REUTILIZABLE
//  ---------------------------------------------------------------------
//  Muestra un compromiso: empresa, concepto (con cuota), monto,
//  vencimiento y estado en color. No sabe de datos ni de navegación:
//  solo dibuja lo que recibe y avisa con "onClick" cuando la tocan.
//
//  CAMBIO: ahora es presionable (abre el detalle) y el texto del
//  concepto con cuota quedó en una función para reutilizarlo.
// =====================================================================

fun colorDeEstado(estado: EstadoCompromiso): Color = when (estado) {
    EstadoCompromiso.PENDIENTE -> ColoresEstado.Pendiente
    EstadoCompromiso.VENCIDO -> ColoresEstado.Vencido
    EstadoCompromiso.PAGADO -> ColoresEstado.Pagado
    EstadoCompromiso.EN_REVISION -> ColoresEstado.EnRevision
    EstadoCompromiso.ANULADO -> ColoresEstado.Anulado
}

/** "Crédito de consumo · cuota 8/24" (o solo el concepto si no es en cuotas). */
@Composable
fun textoConcepto(compromiso: Compromiso): String {
    val cuota = compromiso.numeroCuota
    val total = compromiso.totalCuotas
    return if (cuota != null && total != null) {
        stringResource(R.string.concepto_con_cuota, compromiso.concepto, cuota, total)
    } else {
        compromiso.concepto
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TarjetaCompromiso(
    compromiso: Compromiso,
    nombreEmpresa: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
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
            Text(formatearPesos(compromiso.monto), style = MaterialTheme.typography.titleLarge)
            Text(
                stringResource(R.string.vence_el, formatearFecha(compromiso.fechaVencimiento)),
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
