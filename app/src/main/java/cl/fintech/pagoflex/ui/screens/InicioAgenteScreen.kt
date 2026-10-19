package cl.fintech.pagoflex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.fintech.pagoflex.model.Usuario

/**
 * Pantalla de inicio del agente. Por ahora muestra sus datos y un boton para salir;
 * aqui se incorporara la lista de reportes de los usuarios finales para tomarlos y responderlos.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioAgenteScreen(
    usuario: Usuario,
    onSalir: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reportes") },
                // El boton Salir es un TextButton: su color se define en el boton y no en la barra
                actions = {
                    TextButton(
                        onClick = onSalir,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) { Text("Salir") }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(usuario.nombreCompleto, style = MaterialTheme.typography.headlineSmall)
            Text(usuario.rol.etiqueta, style = MaterialTheme.typography.bodyLarge)
            Text(
                "Aquí verás los reportes de los usuarios finales y podrás responderlos.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
