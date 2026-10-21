
package cl.fintech.pagoflex.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import java.util.Locale

@Composable
fun GraficoCumplimiento(
    porcentaje: Double,
    modifier: Modifier = Modifier
) {
    val valor = porcentaje.toFloat().coerceIn(0f, 100f)
    val progreso = remember { Animatable(0f) }

    val colorFondo = MaterialTheme.colorScheme.surfaceVariant
    val colorCumplimiento = MaterialTheme.colorScheme.primary

    val textoPorcentaje = String.format(
        Locale.forLanguageTag("es-CL"),
        "%.1f %%",
        porcentaje
    )

    LaunchedEffect(valor) {
        progreso.animateTo(
            targetValue = valor,
            animationSpec = tween(durationMillis = 800)
        )
    }

    Box(
        modifier = modifier.size(140.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .size(120.dp)
                .semantics {
                    contentDescription =
                        "Gráfico de cumplimiento: $textoPorcentaje"
                }
        ) {
            val grosor = 12.dp.toPx()

            drawArc(
                color = colorFondo,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(
                    width = grosor,
                    cap = StrokeCap.Round
                )
            )

            if (progreso.value > 0f) {
                drawArc(
                    color = colorCumplimiento,
                    startAngle = -90f,
                    sweepAngle = 360f * progreso.value / 100f,
                    useCenter = false,
                    style = Stroke(
                        width = grosor,
                        cap = StrokeCap.Round
                    )
                )
            }
        }

        Text(
            text = textoPorcentaje,
            style = MaterialTheme.typography.titleMedium
        )
    }
}
