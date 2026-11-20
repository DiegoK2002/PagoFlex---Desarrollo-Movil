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
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
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
import androidx.compose.ui.text.font.FontWeight
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
import cl.fintech.pagoflex.ui.components.indicadorDesplazamiento
import cl.fintech.pagoflex.util.Validaciones
import cl.fintech.pagoflex.viewmodel.RecuperarContrasenaViewModel

@Composable
fun RecuperarContrasenaScreen(
    onVolver: () -> Unit,
    viewModel: RecuperarContrasenaViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colores = MaterialTheme.colorScheme
    val estadoDesplazamiento = rememberScrollState()
    var mostrarContenido by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { mostrarContenido = true }

    val avanceTitulo by animateFloatAsState(
        targetValue = if (mostrarContenido) 1f else 0f,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "entradaTituloRecuperar"
    )
    val avanceIdentidad by animateFloatAsState(
        targetValue = if (mostrarContenido) 1f else 0f,
        animationSpec = tween(360, delayMillis = 45, easing = FastOutSlowInEasing),
        label = "entradaIdentidadRecuperar"
    )
    val avanceContrasena by animateFloatAsState(
        targetValue = if (mostrarContenido) 1f else 0f,
        animationSpec = tween(390, delayMillis = 90, easing = FastOutSlowInEasing),
        label = "entradaContrasenaRecuperar"
    )
    val avanceAcciones by animateFloatAsState(
        targetValue = if (mostrarContenido) 1f else 0f,
        animationSpec = tween(410, delayMillis = 135, easing = FastOutSlowInEasing),
        label = "entradaAccionesRecuperar"
    )
    val avanceExito by animateFloatAsState(
        targetValue = if (uiState.recuperada) 1f else 0f,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "entradaExitoRecuperar"
    )

    Scaffold(
        topBar = { BarraSuperiorVolver("Recuperar contraseña", onVolver) },
        containerColor = colores.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .indicadorDesplazamiento(
                    estado = estadoDesplazamiento,
                    color = colores.primary.copy(alpha = 0.65f)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(estadoDesplazamiento)
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (uiState.recuperada) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer {
                                alpha = avanceExito
                                translationY = (1f - avanceExito) * 12.dp.toPx()
                            },
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = colores.surface),
                        border = BorderStroke(1.dp, colores.outlineVariant),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(RoundedCornerShape(22.dp))
                                    .background(colores.secondaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = colores.onSecondaryContainer,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                            Text(
                                text = "Contraseña actualizada",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = colores.onSurface
                            )
                            Text(
                                text = "Ya puedes iniciar sesión con tu nueva contraseña.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colores.onSurfaceVariant
                            )
                            BotonPrincipal(
                                texto = "Volver al inicio de sesión",
                                onClick = onVolver,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.graphicsLayer {
                            alpha = avanceTitulo
                            translationY = (1f - avanceTitulo) * 8.dp.toPx()
                        },
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            text = "Recupera el acceso",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = colores.onBackground
                        )
                        Text(
                            text = "Verifica tus datos y establece una nueva contraseña.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colores.onSurfaceVariant
                        )
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer {
                                alpha = avanceIdentidad
                                translationY = (1f - avanceIdentidad) * 12.dp.toPx()
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
                            Row(
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
                                        imageVector = Icons.Default.Badge,
                                        contentDescription = null,
                                        tint = colores.onPrimaryContainer,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                    Text(
                                        text = "Confirma tu identidad",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Ingresa los datos registrados en tu cuenta",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = colores.onSurfaceVariant
                                    )
                                }
                            }

                            HorizontalDivider(color = colores.outlineVariant)

                            CampoTexto(
                                valor = uiState.correo,
                                onValorCambia = viewModel::onCorreoCambia,
                                etiqueta = "Correo electrónico",
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
                                    etiqueta = "RUT sin puntos ni guion",
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
                        }
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer {
                                alpha = avanceContrasena
                                translationY = (1f - avanceContrasena) * 12.dp.toPx()
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
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(colores.secondaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = colores.onSecondaryContainer,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                    Text(
                                        text = "Nueva contraseña",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Elige una clave segura para tu cuenta",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = colores.onSurfaceVariant
                                    )
                                }
                            }

                            HorizontalDivider(color = colores.outlineVariant)

                            CampoContrasena(
                                valor = uiState.contrasenaNueva,
                                onValorCambia = viewModel::onContrasenaCambia,
                                etiqueta = "Nueva contraseña",
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
                                etiqueta = "Repetir nueva contraseña",
                                visible = uiState.repetirVisible,
                                onAlternarVisible = viewModel::onAlternarRepetir,
                                error = uiState.errorRepetir
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
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "El correo y el RUT deben pertenecer a la misma cuenta activa.",
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

                    BotonPrincipal(
                        texto = if (uiState.enviando) "Guardando..." else "Restablecer contraseña",
                        onClick = viewModel::onRecuperar,
                        habilitado = !uiState.enviando,
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
    }
}
