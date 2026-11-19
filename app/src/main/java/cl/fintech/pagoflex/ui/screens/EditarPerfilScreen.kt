package cl.fintech.pagoflex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.fintech.pagoflex.model.Usuario
import cl.fintech.pagoflex.ui.components.BarraSuperiorVolver
import cl.fintech.pagoflex.ui.components.BotonPrincipal
import cl.fintech.pagoflex.ui.components.BotonSecundario
import cl.fintech.pagoflex.ui.components.CampoTexto
import cl.fintech.pagoflex.ui.components.ListaDesplegable
import cl.fintech.pagoflex.viewmodel.EditarPerfilViewModel

@Composable
fun EditarPerfilScreen(
    usuario: Usuario,
    onUsuarioActualizado: (Usuario) -> Unit,
    onVolver: () -> Unit,
    viewModel: EditarPerfilViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Precarga los datos actuales una sola vez
    LaunchedEffect(usuario.id) {
        viewModel.cargar(usuario)
    }

    Scaffold(
        topBar = { BarraSuperiorVolver("Editar datos personales", onVolver) },
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
                    CampoTexto(
                        valor = uiState.nombres,
                        onValorCambia = viewModel::onNombresCambia,
                        etiqueta = "Nombres",
                        icono = Icons.Default.Person,
                        error = uiState.errorNombres,
                        capitalizacion = KeyboardCapitalization.Words
                    )

                    CampoTexto(
                        valor = uiState.apellidos,
                        onValorCambia = viewModel::onApellidosCambia,
                        etiqueta = "Apellidos",
                        icono = Icons.Default.Person,
                        error = uiState.errorApellidos,
                        capitalizacion = KeyboardCapitalization.Words
                    )

                    CampoTexto(
                        valor = uiState.correo,
                        onValorCambia = viewModel::onCorreoCambia,
                        etiqueta = "Correo electr\u00f3nico",
                        icono = Icons.Default.Email,
                        error = uiState.errorCorreo,
                        tipoTeclado = KeyboardType.Email
                    )

                    ListaDesplegable(
                        etiqueta = "Comuna",
                        opciones = uiState.comunas,
                        seleccionada = uiState.comuna.ifEmpty { null },
                        textoDe = { it },
                        onSeleccionar = viewModel::onComunaSeleccionada,
                        error = uiState.errorComuna
                    )

                    Text(
                        text = "El RUT no se puede modificar.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (uiState.errorGeneral != null) {
                        Text(
                            text = uiState.errorGeneral!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    if (uiState.guardado) {
                        Text(
                            text = "Datos actualizados correctamente.",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    BotonPrincipal(
                        texto = if (uiState.guardando) "Guardando..." else "Guardar cambios",
                        onClick = { viewModel.onGuardar(onUsuarioActualizado) },
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
