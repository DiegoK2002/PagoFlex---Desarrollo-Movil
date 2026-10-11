package cl.app.pagoflex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cl.app.pagoflex.R
import cl.app.pagoflex.model.Usuario
import cl.app.pagoflex.ui.theme.Dimens

// =====================================================================
//  InicioProvisorioScreen.kt  —  PANTALLA TEMPORAL DE AGENTE Y EJECUTIVO
//  ---------------------------------------------------------------------
//  Sirve para comprobar que cada rol entra a su propia pantalla.
//  Se reemplaza por las pantallas reales (reportes del agente,
//  compromisos y resumen de cobranza del ejecutivo).
// =====================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioProvisorioScreen(
    titulo: String,
    usuario: Usuario,
    onSalir: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(titulo) },
                actions = {
                    TextButton(onClick = onSalir) {
                        Text(stringResource(R.string.btn_salir))
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(Dimens.espacioPantalla),
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
        ) {
            Text(usuario.nombreCompleto, style = MaterialTheme.typography.headlineMedium)
            Text(usuario.rol.etiqueta, style = MaterialTheme.typography.bodyLarge)
            Text(stringResource(R.string.en_construccion), style = MaterialTheme.typography.bodyMedium)
        }
    }
}
