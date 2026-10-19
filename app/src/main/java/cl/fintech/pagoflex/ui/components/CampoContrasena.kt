package cl.fintech.pagoflex.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import cl.fintech.pagoflex.ui.styles.EstilosCampo
import cl.fintech.pagoflex.ui.theme.Dimens

/**
 * Campo para escribir una contrasena. Muestra puntos en lugar del texto y tiene un boton
 * con forma de ojo para mostrarla u ocultarla.
 *
 * @param visible si es true, el texto se ve; si es false, se oculta.
 * @param onAlternarVisible se llama al tocar el ojo; quien usa el campo decide el nuevo valor de [visible].
 * @param error mensaje que se muestra bajo el campo; null significa que no hay error.
 */
@Composable
fun CampoContrasena(
    valor: String,
    onValorCambia: (String) -> Unit,
    etiqueta: String,
    visible: Boolean,
    onAlternarVisible: () -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorCambia,
        modifier = modifier.fillMaxWidth(),
        label = { Text(etiqueta) },
        // Candado a la izquierda
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                modifier = Modifier.size(Dimens.tamanoIcono)
            )
        },
        // Ojo a la derecha: cambia segun la contrasena este visible u oculta
        trailingIcon = {
            IconButton(onClick = onAlternarVisible) {
                Icon(
                    imageVector = if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (visible) "Ocultar contrase\u00f1a" else "Mostrar contrase\u00f1a",
                    modifier = Modifier.size(Dimens.tamanoIcono)
                )
            }
        },
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        isError = error != null,
        supportingText = { if (error != null) Text(error) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        shape = EstilosCampo.forma,
        colors = EstilosCampo.colores()
    )
}
