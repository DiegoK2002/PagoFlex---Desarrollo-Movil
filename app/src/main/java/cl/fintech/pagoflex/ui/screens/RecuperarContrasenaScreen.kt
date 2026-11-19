package cl.fintech.pagoflex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.fintech.pagoflex.ui.components.BarraSuperiorVolver
import cl.fintech.pagoflex.ui.components.BotonPrincipal
import cl.fintech.pagoflex.ui.components.CampoContrasena
import cl.fintech.pagoflex.ui.components.CampoTexto
import cl.fintech.pagoflex.ui.components.RequisitosContrasena
import cl.fintech.pagoflex.util.Validaciones
import cl.fintech.pagoflex.viewmodel.RecuperarContrasenaViewModel

@Composable
fun RecuperarContrasenaScreen(
    onVolver: () -> Unit,
    viewModel: RecuperarContrasenaViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { BarraSuperiorVolver("Recuperar contrase\u00f1a", onVolver) },
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

            // Confirmacion final: reemplaza al formulario
            if (uiState.recuperada) {
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
                        Text(
                            text = "Contrase\u00f1a actualizada",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Ya puedes iniciar sesi\u00f3n con tu nueva contrase\u00f1a.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        BotonPrincipal(
                            texto = "Volver al inicio de sesi\u00f3n",
                            onClick = onVolver,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                return@Column
            }

            Text(
                text = "Para restablecer tu contrase\u00f1a confirma tu identidad con el correo y el RUT de tu cuenta.",
                style = MaterialTheme.typography.bodyMedium
            )

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
                    CampoTexto(
                        valor = uiState.correo,
                        onValorCambia = viewModel::onCorreoCambia,
                        etiqueta = "Correo electr\u00f3nico",
                        icono = Icons.Default.Email,
                        error = uiState.errorCorreo,
                        tipoTeclado = KeyboardType.Email
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        CampoTexto(
                            valor = uiState.rutNumero,
                            onValorCambia = viewModel::onRutNumeroCambia,
                            etiqueta = "RUT (sin puntos ni gui\u00f3n)",
                            icono = Icons.Default.Badge,
                            error = uiState.errorRut,
                            tipoTeclado = KeyboardType.Number,
                            modifier = Modifier.weight(2.2f)
                        )

                        CampoTexto(
                            valor = uiState.rutDv,
                            onValorCambia = viewModel::onRutDvCambia,
                            etiqueta = "DV",
                            icono = Icons.Default.Check,
                            error = uiState.errorRut,
                            mostrarMensajeError = false,
                            capitalizacion = KeyboardCapitalization.Characters,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    CampoContrasena(
                        valor = uiState.contrasenaNueva,
                        onValorCambia = viewModel::onContrasenaCambia,
                        etiqueta = "Nueva contrase\u00f1a",
                        visible = uiState.contrasenaVisible,
                        onAlternarVisible = viewModel::onAlternarContrasena,
                        error = uiState.errorContrasena
                    )

                    RequisitosContrasena(
                        requisitos = Validaciones.requisitosContrasena(
                            uiState.contrasenaNueva,
                            uiState.correo,
                            ""
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

                    BotonPrincipal(
                        texto = if (uiState.enviando) "Guardando..." else "Restablecer contrase\u00f1a",
                        onClick = viewModel::onRecuperar,
                        habilitado = !uiState.enviando,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
