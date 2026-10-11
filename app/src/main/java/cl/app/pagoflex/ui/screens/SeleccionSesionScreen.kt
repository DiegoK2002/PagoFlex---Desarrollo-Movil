package cl.app.pagoflex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cl.app.pagoflex.R
import cl.app.pagoflex.model.Usuario
import cl.app.pagoflex.ui.styles.EstilosTarjeta
import cl.app.pagoflex.ui.theme.Dimens

// =====================================================================
//  SeleccionSesionScreen.kt  —  PANTALLA INICIAL: ELEGIR SESIÓN SIMULADA
//  ---------------------------------------------------------------------
//  El caso permite simular un usuario ya autenticado (no hay login real).
//  Aquí se elige con qué persona entrar: un usuario final, el agente o
//  la ejecutiva. Según el rol, la app muestra pantallas distintas.
//  No tiene ViewModel propio: recibe la lista y avisa con una lambda.
// =====================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeleccionSesionScreen(
    sesiones: List<Usuario>,
    onElegir: (Usuario) -> Unit
) {
    Scaffold { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(Dimens.espacioPantalla),
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)) {
                    Text(
                        stringResource(R.string.app_name),
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        stringResource(R.string.subtitulo_seleccion),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            items(sesiones) { usuario ->
                Card(
                    onClick = { onElegir(usuario) },
                    shape = EstilosTarjeta.forma,
                    colors = EstilosTarjeta.colores(),
                    elevation = EstilosTarjeta.elevacion(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(Dimens.espacioMedio),
                        verticalArrangement = Arrangement.spacedBy(Dimens.espacioMini)
                    ) {
                        Text(usuario.nombreCompleto, style = MaterialTheme.typography.titleMedium)
                        Text(usuario.rol.etiqueta, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
