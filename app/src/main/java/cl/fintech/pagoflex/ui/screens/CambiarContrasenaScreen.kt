package cl.fintech.pagoflex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.fintech.pagoflex.model.Usuario
import cl.fintech.pagoflex.ui.components.BarraSuperiorVolver
import cl.fintech.pagoflex.ui.components.BotonPrincipal
import cl.fintech.pagoflex.ui.components.BotonSecundario
import cl.fintech.pagoflex.ui.components.CampoContrasena
import cl.fintech.pagoflex.ui.components.RequisitosContrasena
import cl.fintech.pagoflex.util.Validaciones
import cl.fintech.pagoflex.viewmodel.CambiarContrasenaViewModel

@Composable
fun CambiarContrasenaScreen(
    usuario: Usuario,
    onVolver: () -> Unit,
    viewModel: CambiarContrasenaViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { BarraSuperiorVolver("Cambiar contrase\u00f1a", onVolver) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CampoContrasena(
                        valor = uiState.contrasenaActual,
                        onValorCambia = viewModel::onActualCambia,
                        etiqueta = "Contrase\u00f1a actual",
                        visible = uiState.actualVisible,
                        onAlternarVisible = viewModel::onAlternarActual,
                        error = uiState.errorActual
                    )

                    CampoContrasena(
                        valor = uiState.contrasenaNueva,
                        onValorCambia = viewModel::onNuevaCambia,
                        etiqueta = "Nueva contrase\u00f1a",
                        visible = uiState.nuevaVisible,
                        onAlternarVisible = viewModel::onAlternarNueva,
                        error = uiState.errorNueva
                    )

                    RequisitosContrasena(
                        requisitos = Validaciones.requisitosContrasena(
                            uiState.contrasenaNueva,
                            usuario.correo,
                            usuario.nombres
                        )
                    )

                    CampoContrasena(
                        valor = uiState.repetirContrasena,
                        onValorCambia = viewModel::onRepetirCambia,
                        etiqueta = "Repetir nueva contrase\u00f1a",
                        visible = uiState.repetirVisible,
                        onAlternarVisible = viewModel::onAlternarRepetir,
                        error = uiState.errorRepetir
                    )

                    if (uiState.errorGeneral != null) {
                        Text(
                            text = uiState.errorGeneral!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    if (uiState.cambiada) {
                        Text(
                            text = "Contrase\u00f1a actualizada correctamente.",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    BotonPrincipal(
                        texto = if (uiState.guardando) "Guardando..." else "Cambiar contrase\u00f1a",
                        onClick = { viewModel.onCambiar(usuario) },
                        habilitado = !uiState.guardando,
                        modifier = Modifier.fillMaxWidth()
                    )

                    BotonSecundario(
                        texto = "Volver",
                        onClick = onVolver,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
