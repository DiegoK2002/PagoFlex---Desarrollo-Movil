package cl.fintech.pagoflex.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import cl.fintech.pagoflex.ui.styles.EstilosCampo
import cl.fintech.pagoflex.ui.theme.Dimens

/**
 * Campo de texto de una linea con icono a la izquierda y mensaje de error opcional.
 *
 * @param error mensaje que se muestra bajo el campo; null significa que no hay error.
 * @param tipoTeclado tipo de teclado del telefono. Con [KeyboardType.Number] solo aparecen numeros.
 * @param capitalizacion indica si el teclado escribe en mayusculas (por ejemplo, para el digito verificador).
 * @param mostrarMensajeError si es false, el campo se marca en rojo pero no repite el mensaje
 *        (sirve cuando dos campos comparten un mismo error).
 */
@Composable
fun CampoTexto(
    valor: String,
    onValorCambia: (String) -> Unit,
    etiqueta: String,
    icono: ImageVector,
    modifier: Modifier = Modifier,
    error: String? = null,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    capitalizacion: KeyboardCapitalization = KeyboardCapitalization.None,
    mostrarMensajeError: Boolean = true
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorCambia,
        modifier = modifier.fillMaxWidth(),
        label = { Text(etiqueta) },
        // Icono decorativo a la izquierda del texto
        leadingIcon = {
            Icon(
                imageVector = icono,
                contentDescription = null,
                modifier = Modifier.size(Dimens.tamanoIcono)
            )
        },
        isError = error != null,                                   // borde y etiqueta en rojo si hay error
        supportingText = { if (error != null && mostrarMensajeError) Text(error) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = tipoTeclado,
            capitalization = capitalizacion,
            imeAction = ImeAction.Next                             // la tecla de accion pasa al campo siguiente
        ),
        shape = EstilosCampo.forma,
        colors = EstilosCampo.colores()
    )
}
