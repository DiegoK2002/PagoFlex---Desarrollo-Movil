package cl.fintech.pagoflex.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

/**
 * Muestra un dato con su nombre: la etiqueta en letra pequena arriba y el valor debajo.
 *
 * @param destacado si es true, el valor se muestra mas grande y en negrita.
 * @param colorValor color del valor; con [Color.Unspecified] se usa el color normal del texto.
 */
@Composable
fun FilaDato(
    etiqueta: String,
    valor: String,
    modifier: Modifier = Modifier,
    destacado: Boolean = false,
    colorValor: Color = Color.Unspecified
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Nombre del dato, con color atenuado
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        // Valor del dato
        Text(
            text = valor,
            style = if (destacado) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyLarge,
            fontWeight = if (destacado) FontWeight.Bold else null,
            color = colorValor
        )
    }
}
