package cl.app.pagoflex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import cl.app.pagoflex.model.Compromiso
import cl.app.pagoflex.model.Usuario
import cl.app.pagoflex.ui.components.TarjetaCompromiso
import cl.app.pagoflex.ui.theme.Dimens

// =====================================================================
//  InicioUsuarioScreen.kt  —  INICIO DEL USUARIO FINAL (RF-02, RF-03)
//  ---------------------------------------------------------------------
//  Lista de compromisos con LazyColumn (dibuja solo los visibles).
//  CAMBIO: al tocar una tarjeta se abre el detalle. "onVerDetalle"
//  entrega el FOLIO del compromiso tocado (viaja en la ruta).
//  Falta: resumen del mes (total a pagar, próximo vencimiento).
// =====================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioUsuarioScreen(
    usuario: Usuario,
    compromisos: List<Compromiso>,
    nombreEmpresa: (String) -> String,
    onVerDetalle: (String) -> Unit,
    onSalir: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.titulo_mis_compromisos)) },
                actions = {
                    TextButton(onClick = onSalir) {
                        Text(stringResource(R.string.btn_salir))
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(Dimens.espacioMedio),
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)
        ) {
            item {
                Text(
                    stringResource(R.string.saludo, usuario.nombres.substringBefore(' ')),
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            items(compromisos) { compromiso ->
                TarjetaCompromiso(
                    compromiso = compromiso,
                    nombreEmpresa = nombreEmpresa(compromiso.codigoEmpresa),
                    onClick = { onVerDetalle(compromiso.folio) }
                )
            }
        }
    }
}
