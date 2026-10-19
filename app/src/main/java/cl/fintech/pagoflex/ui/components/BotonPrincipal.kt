package cl.fintech.pagoflex.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import cl.fintech.pagoflex.ui.styles.EstilosBoton
import cl.fintech.pagoflex.ui.styles.estiloAltoBoton

/**
 * Boton de la accion principal de una pantalla: fondo del color primario y sombra.
 *
 * @param habilitado si es false, el boton se ve atenuado y no responde al toque.
 * @param icono icono opcional (por ejemplo Icons.Default.Check) que se muestra antes del texto.
 */
@Composable
fun BotonPrincipal(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
    icono: ImageVector? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier.estiloAltoBoton(),
        enabled = habilitado,
        shape = EstilosBoton.forma,
        colors = EstilosBoton.coloresPrincipal(),
        elevation = EstilosBoton.elevacion(),
        contentPadding = EstilosBoton.relleno
    ) {
        // El icono y su separacion solo se dibujan si se entrego un icono
        if (icono != null) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        }
        Text(text = texto)
    }
}
