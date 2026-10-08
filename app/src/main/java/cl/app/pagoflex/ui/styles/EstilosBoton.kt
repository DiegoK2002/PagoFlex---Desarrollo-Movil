package cl.app.pagoflex.ui.styles

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.app.pagoflex.ui.theme.Dimens
import cl.app.pagoflex.ui.theme.GrisDeshabilitado

object EstilosBoton {
    val forma = RoundedCornerShape(Dimens.radioBoton)
    val relleno = PaddingValues(20.dp, 12.dp)

    // Botón principal: relleno del color primario del tema
    @Composable
    fun colorPrincipal(): ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        disabledContainerColor = GrisDeshabilitado,
        disabledContentColor = MaterialTheme.colorScheme.onPrimary
    )

    // Botón secundario: solo borde y texto del color primario
    @Composable
    fun coloresSecundarios(): ButtonColors = ButtonDefaults.outlinedButtonColors(
        contentColor = MaterialTheme.colorScheme.primary
    )

    @Composable
    fun bordesSecundarios(): BorderStroke = BorderStroke(
        Dimens.bordeBoton, MaterialTheme.colorScheme.primary
    )

    @Composable
    fun elevacion(): ButtonElevation = ButtonDefaults.buttonElevation(
        defaultElevation = Dimens.elevacionBoton,
        pressedElevation = 1.dp
    )
}

fun Modifier.estiloAltoBoton(): Modifier = this.height(Dimens.alturaBoton)
