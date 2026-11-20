package cl.fintech.pagoflex.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cl.fintech.pagoflex.model.Compromiso
import cl.fintech.pagoflex.model.EstadoCompromiso
import cl.fintech.pagoflex.ui.theme.ColoresEstado
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
) {
    val origenInteraccion = remember { MutableInteractionSource() }
    val estaPresionada by origenInteraccion.collectIsPressedAsState()
    val escala by animateFloatAsState(
        targetValue = if (estaPresionada) 0.987f else 1f,
        animationSpec = tween(
            durationMillis = if (estaPresionada) 90 else 190,
            easing = FastOutSlowInEasing
        ),
        label = "PresionTarjetaCompromiso"
    )

    val esOscuro = isSystemInDarkTheme()
    val colorEstado = if (esOscuro) {
        when (compromiso.estado) {
            EstadoCompromiso.PENDIENTE -> Color(0xFFFFD47A)
            EstadoCompromiso.VENCIDO -> Color(0xFFFFAAA3)
            EstadoCompromiso.PAGADO -> Color(0xFF78E6BA)
            EstadoCompromiso.EN_REVISION -> Color(0xFFBEC5FF)
            EstadoCompromiso.ANULADO -> Color(0xFFCAD2E2)
        }
    } else {
        colorDeEstado(compromiso.estado)
    }

    val avisoRecargo = when {
        importe == compromiso.monto -> null
        compromiso.estado == EstadoCompromiso.PAGADO -> "Se pagó con recargo por atraso"
        else -> "Incluye recargo por atraso"
    }

    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = escala
                scaleY = escala
            },
        interactionSource = origenInteraccion,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .background(colorEstado)
        )

        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = nombreEmpresa.trim().take(1).uppercase().ifEmpty { "P" },
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Text(
                    text = nombreEmpresa,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Surface(
                shape = RoundedCornerShape(50),
                color = colorEstado.copy(alpha = if (esOscuro) 0.18f else 0.11f)
            ) {
                Text(
                    text = compromiso.estado.etiqueta,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorEstado
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = textoConcepto(compromiso),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Crossfade(
                    targetState = importeConvertido ?: formatearPesos(importe),
                    animationSpec = tween(durationMillis = 240),
                    label = "CambioMonedaCompromiso"
                ) { montoVisible ->
                    Text(
                        text = montoVisible,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (importeConvertido != null) {
                    Text(
                        text = "Equivalente aproximado · Importe real: ${formatearPesos(importe)} CLP",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = "Monto en pesos chilenos",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (avisoRecargo != null) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.errorContainer
                ) {
                    Text(
                        text = avisoRecargo,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "VENCIMIENTO",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatearFecha(compromiso.fechaVencimiento),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "Ver detalle  ›",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
