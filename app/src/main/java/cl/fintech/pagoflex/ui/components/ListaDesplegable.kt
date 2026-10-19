package cl.fintech.pagoflex.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cl.fintech.pagoflex.ui.styles.EstilosCampo

/**
 * Lista desplegable para elegir una opcion de varias. Funciona con cualquier tipo [T].
 *
 * @param seleccionada opcion elegida; si es null el campo se muestra vacio.
 * @param textoDe convierte una opcion al texto que se muestra.
 * @param error mensaje que se muestra bajo el campo; null significa que no hay error.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> ListaDesplegable(
    etiqueta: String,
    opciones: List<T>,
    seleccionada: T?,
    textoDe: (T) -> String,
    onSeleccionar: (T) -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null
) {
    // Indica si el menu de opciones esta abierto
    var expandido by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expandido,
        onExpandedChange = { expandido = it },
        modifier = modifier.fillMaxWidth()
    ) {
        // Campo de solo lectura que muestra la opcion elegida y abre el menu al tocarlo
        OutlinedTextField(
            value = if (seleccionada != null) textoDe(seleccionada) else "",
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            readOnly = true,
            label = { Text(etiqueta) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
            isError = error != null,
            supportingText = { if (error != null) Text(error) },
            singleLine = true,
            shape = EstilosCampo.forma,
            colors = EstilosCampo.colores()
        )

        // Menu con una fila por cada opcion; al elegir una se avisa y se cierra
        ExposedDropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false }
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(textoDe(opcion)) },
                    onClick = {
                        onSeleccionar(opcion)
                        expandido = false
                    }
                )
            }
        }
    }
}
