package cl.fintech.pagoflex.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.fintech.pagoflex.ui.theme.ColoresEstado
import cl.fintech.pagoflex.util.Requisito

/**
 * Lista de condiciones de la contrasena. Cada una se marca con un visto verde cuando se cumple
 * y con una cruz gris mientras no se cumple, para que la persona vea que le falta.
 */
@Composable
fun RequisitosContrasena(
    requisitos: List<Requisito>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        // Una fila por requisito: icono de estado y texto
        requisitos.forEach { requisito ->
            val color = if (requisito.cumplido) ColoresEstado.Pagado else MaterialTheme.colorScheme.onSurfaceVariant
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = if (requisito.cumplido) Icons.Default.Check else Icons.Default.Close,
                    contentDescription = if (requisito.cumplido) "Cumplido" else "Pendiente",
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = requisito.texto,
                    style = MaterialTheme.typography.bodySmall,
                    color = color
                )
            }
        }
    }
}
