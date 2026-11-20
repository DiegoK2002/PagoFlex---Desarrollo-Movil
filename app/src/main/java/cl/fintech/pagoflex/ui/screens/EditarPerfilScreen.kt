package cl.fintech.pagoflex.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
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
    val colores = MaterialTheme.colorScheme
    var mostrarContenido by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { mostrarContenido = true }

    val avanceTitulo by animateFloatAsState(
        targetValue = if (mostrarContenido) 1f else 0f,
        animationSpec = tween(320, easing = FastOutSlowInEasing),
        label = "entradaTituloEditarPerfil"
    )
    val avancePersonales by animateFloatAsState(
        targetValue = if (mostrarContenido) 1f else 0f,
        animationSpec = tween(370, delayMillis = 50, easing = FastOutSlowInEasing),
        label = "entradaDatosPersonalesEditarPerfil"
    )
    val avanceContacto by animateFloatAsState(
        targetValue = if (mostrarContenido) 1f else 0f,
        animationSpec = tween(390, delayMillis = 95, easing = FastOutSlowInEasing),
        label = "entradaContactoEditarPerfil"
    )
    val avanceAcciones by animateFloatAsState(
        targetValue = if (mostrarContenido) 1f else 0f,
        animationSpec = tween(420, delayMillis = 140, easing = FastOutSlowInEasing),
        label = "entradaAccionesEditarPerfil"
    )

    LaunchedEffect(usuario.id) {
        viewModel.cargar(usuario)
    }

    Scaffold(
        topBar = { BarraSuperiorVolver("Editar datos personales", onVolver) },
        containerColor = colores.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier.graphicsLayer {
                    alpha = avanceTitulo
                    translationY = (1f - avanceTitulo) * 8.dp.toPx()
                },
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = "Mantén tus datos al día",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = colores.onBackground
                )
                Text(
                    text = "Actualiza solo lo que necesites. Los cambios se guardarán en tu cuenta.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colores.onSurfaceVariant
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        alpha = avancePersonales
                        translationY = (1f - avancePersonales) * 12.dp.toPx()
                    },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = colores.surface),
                border = BorderStroke(1.dp, colores.outlineVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    EncabezadoSeccionEditarPerfil(
                        icono = Icons.Default.Person,
                        titulo = "Datos personales",
                        subtitulo = "El nombre que aparece en tu perfil"
                    )
                    HorizontalDivider(color = colores.outlineVariant)
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
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        alpha = avanceContacto
                        translationY = (1f - avanceContacto) * 12.dp.toPx()
                    },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = colores.surface),
                border = BorderStroke(1.dp, colores.outlineVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    EncabezadoSeccionEditarPerfil(
                        icono = Icons.Default.Email,
                        titulo = "Contacto",
                        subtitulo = "Correo y comuna asociados a tu cuenta"
                    )
                    HorizontalDivider(color = colores.outlineVariant)
                    CampoTexto(
                        valor = uiState.correo,
                        onValorCambia = viewModel::onCorreoCambia,
                        etiqueta = "Correo electrónico",
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
                }
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        alpha = avanceAcciones
                        translationY = (1f - avanceAcciones) * 8.dp.toPx()
                    },
                color = colores.primaryContainer.copy(alpha = 0.55f),
                contentColor = colores.onPrimaryContainer,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Tu RUT está vinculado a la cuenta y no puede modificarse aquí.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            uiState.errorGeneral?.let { error ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = colores.errorContainer,
                    contentColor = colores.onErrorContainer,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(14.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if (uiState.guardado) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = colores.primaryContainer,
                    contentColor = colores.onPrimaryContainer,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Datos actualizados correctamente.",
                        modifier = Modifier.padding(14.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            BotonPrincipal(
                texto = if (uiState.guardando) "Guardando..." else "Guardar cambios",
                onClick = { viewModel.onGuardar(onUsuarioActualizado) },
                habilitado = !uiState.guardando,
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        alpha = avanceAcciones
                        translationY = (1f - avanceAcciones) * 8.dp.toPx()
                    }
            )

            BotonSecundario(
                texto = "Volver",
                onClick = onVolver,
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        alpha = avanceAcciones
                        translationY = (1f - avanceAcciones) * 8.dp.toPx()
                    }
            )
        }
    }
}

@Composable
private fun EncabezadoSeccionEditarPerfil(
    icono: ImageVector,
    titulo: String,
    subtitulo: String
) {
    val colores = MaterialTheme.colorScheme
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(colores.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = colores.onPrimaryContainer,
                modifier = Modifier.size(22.dp)
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = colores.onSurface
            )
            Text(
                text = subtitulo,
                style = MaterialTheme.typography.bodySmall,
                color = colores.onSurfaceVariant
            )
        }
    }
}
