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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.fintech.pagoflex.model.Usuario
import cl.fintech.pagoflex.ui.components.BarraSuperiorVolver
import cl.fintech.pagoflex.ui.components.BotonPrincipal
import cl.fintech.pagoflex.ui.components.BotonSecundario
import cl.fintech.pagoflex.ui.components.CampoContrasena
import cl.fintech.pagoflex.ui.components.RequisitosContrasena
import cl.fintech.pagoflex.ui.components.indicadorDesplazamiento
import cl.fintech.pagoflex.util.Validaciones
import cl.fintech.pagoflex.viewmodel.CambiarContrasenaViewModel

@Composable
fun CambiarContrasenaScreen(
    usuario: Usuario,
    onVolver: () -> Unit,
    viewModel: CambiarContrasenaViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colores = MaterialTheme.colorScheme
    val estadoDesplazamiento = rememberScrollState()
    var mostrarContenido by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { mostrarContenido = true }

    val avanceTitulo by animateFloatAsState(
        targetValue = if (mostrarContenido) 1f else 0f,
        animationSpec = tween(320, easing = FastOutSlowInEasing),
        label = "entradaTituloCambiarContrasena"
    )
    val avanceFormulario by animateFloatAsState(
        targetValue = if (mostrarContenido) 1f else 0f,
        animationSpec = tween(370, delayMillis = 50, easing = FastOutSlowInEasing),
        label = "entradaFormularioCambiarContrasena"
    )
    val avanceAviso by animateFloatAsState(
        targetValue = if (mostrarContenido) 1f else 0f,
        animationSpec = tween(390, delayMillis = 95, easing = FastOutSlowInEasing),
        label = "entradaAvisoCambiarContrasena"
    )
    val avanceAcciones by animateFloatAsState(
        targetValue = if (mostrarContenido) 1f else 0f,
        animationSpec = tween(420, delayMillis = 140, easing = FastOutSlowInEasing),
        label = "entradaAccionesCambiarContrasena"
    )

    Scaffold(
        topBar = { BarraSuperiorVolver("Cambiar contraseña", onVolver) },
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
                Column(
                    modifier = Modifier.graphicsLayer {
                        alpha = avanceTitulo
                        translationY = (1f - avanceTitulo) * 8.dp.toPx()
                    },
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = "Protege tu cuenta",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = colores.onBackground
                    )
                    Text(
                        text = "Actualiza tu contraseña para mantener tu información segura.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colores.onSurfaceVariant
                    )
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            alpha = avanceFormulario
                            translationY = (1f - avanceFormulario) * 12.dp.toPx()
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
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = colores.onPrimaryContainer,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                Text(
                                    text = "Seguridad de la cuenta",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colores.onSurface
                                )
                                Text(
                                    text = "Confirma tu identidad y define una clave nueva",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colores.onSurfaceVariant
                                )
                            }
                        }

                        HorizontalDivider(color = colores.outlineVariant)

                        CampoContrasena(
                            valor = uiState.contrasenaActual,
                            onValorCambia = viewModel::onActualCambia,
                            etiqueta = "Contraseña actual",
                            visible = uiState.actualVisible,
                            onAlternarVisible = viewModel::onAlternarActual,
                            error = uiState.errorActual
                        )

                        HorizontalDivider(color = colores.outlineVariant)

                        CampoContrasena(
                            valor = uiState.contrasenaNueva,
                            onValorCambia = viewModel::onNuevaCambia,
                            etiqueta = "Nueva contraseña",
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
                            alpha = avanceAviso
                            translationY = (1f - avanceAviso) * 8.dp.toPx()
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
                            text = "La nueva contraseña debe ser diferente de la actual.",
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

                if (uiState.cambiada) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = colores.secondaryContainer,
                        contentColor = colores.onSecondaryContainer,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Contraseña actualizada correctamente.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                BotonPrincipal(
                    texto = if (uiState.guardando) "Guardando..." else "Cambiar contraseña",
                    onClick = { viewModel.onCambiar(usuario) },
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
}
