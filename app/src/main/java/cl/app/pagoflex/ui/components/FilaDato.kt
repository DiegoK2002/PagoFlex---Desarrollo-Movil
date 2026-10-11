package cl.app.pagoflex.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

// =====================================================================
//  FilaDato.kt  —  COMPONENTE REUTILIZABLE: "ETIQUETA + VALOR"
//  ---------------------------------------------------------------------
//  Etiqueta pequeña arriba y valor debajo. Se usa en el detalle del
//  compromiso y en el comprobante. "destacado" agranda el valor.
// =====================================================================

@Composable
fun FilaDato(
    etiqueta: String,
    valor: String,
    modifier: Modifier = Modifier,
    destacado: Boolean = false,
    colorValor: Color = Color.Unspecified
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = valor,
            style = if (destacado) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyLarge,
            fontWeight = if (destacado) FontWeight.Bold else null,
            color = colorValor
        )
    }
}
