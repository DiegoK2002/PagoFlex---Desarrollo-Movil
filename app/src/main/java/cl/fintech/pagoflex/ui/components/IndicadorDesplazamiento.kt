package cl.fintech.pagoflex.ui.components

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private class MedidasDelIndicador {
    var cantidadElementos = 0
    val alturas = mutableMapOf<Int, Int>()
}

fun Modifier.indicadorDesplazamiento(
    estado: LazyListState,
    color: Color
): Modifier = composed {
    val medidas = remember(estado) { MedidasDelIndicador() }

    drawWithContent {
        drawContent()

        if (!estado.canScrollBackward && !estado.canScrollForward) {
            return@drawWithContent
        }

        val info = estado.layoutInfo
        val total = info.totalItemsCount
        if (total <= 0 || info.visibleItemsInfo.isEmpty()) {
            return@drawWithContent
        }

        if (medidas.cantidadElementos != total) {
            medidas.cantidadElementos = total
            medidas.alturas.clear()
        }

        val separacion = info.mainAxisItemSpacing.coerceAtLeast(0)
        info.visibleItemsInfo.forEach { elemento ->
            medidas.alturas[elemento.index] = (elemento.size + separacion).coerceAtLeast(1)
        }

        val alturasConocidas = medidas.alturas.values.sorted()
        val alturaEstimada = alturasConocidas[alturasConocidas.size / 2].toFloat()
        val elementosSinMedir = (total - medidas.alturas.size).coerceAtLeast(0)

        val altoContenido = (
                medidas.alturas.values.sumOf { it.toLong() }.toFloat() +
                        elementosSinMedir * alturaEstimada +
                        info.beforeContentPadding + info.afterContentPadding - separacion
                ).coerceAtLeast(size.height)

        val altoVisible = size.height
        val rangoDesplazamiento = (altoContenido - altoVisible).coerceAtLeast(1f)

        var desplazamiento = estado.firstVisibleItemScrollOffset.toFloat()
        for (indice in 0 until estado.firstVisibleItemIndex) {
            desplazamiento += medidas.alturas[indice]?.toFloat() ?: alturaEstimada
        }

        val avance = when {
            !estado.canScrollBackward -> 0f
            !estado.canScrollForward -> 1f
            else -> (desplazamiento / rangoDesplazamiento).coerceIn(0f, 1f)
        }

        val margen = 6.dp.toPx()
        val grosor = 3.dp.toPx()
        val altoPista = (size.height - 2f * margen).coerceAtLeast(0f)
        if (altoPista <= 0f) return@drawWithContent

        val altoIndicador = (altoPista * altoVisible / altoContenido)
            .coerceIn(28.dp.toPx().coerceAtMost(altoPista), altoPista)
        val posicionY = margen + (altoPista - altoIndicador) * avance
        val posicionX = (size.width - grosor - 3.dp.toPx()).coerceAtLeast(0f)

        drawRoundRect(
            color = color,
            topLeft = Offset(posicionX, posicionY),
            size = Size(grosor, altoIndicador),
            cornerRadius = CornerRadius(grosor / 2f)
        )
    }
}
