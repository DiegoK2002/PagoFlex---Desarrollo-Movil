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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.fintech.pagoflex.model.SesionUiState
import cl.fintech.pagoflex.model.Usuario
import cl.fintech.pagoflex.ui.components.BotonPrincipal
import cl.fintech.pagoflex.ui.components.BotonSecundario
import cl.fintech.pagoflex.ui.components.CampoContrasena
import cl.fintech.pagoflex.ui.components.CampoTexto
import cl.fintech.pagoflex.viewmodel.InicioSesionViewModel

@Composable
fun InicioSesionScreen(
    onInicioExitoso: (Usuario) -> Unit,
    onCrearCuenta: () -> Unit,
    onRecuperarContrasena: () -> Unit,
    viewModel: InicioSesionViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    InicioSesionContent(
        uiState = uiState,
        onCorreoCambia = viewModel::onCorreoCambia,
        onContrasenaCambia = viewModel::onContrasenaCambia,
        onAlternarVisibilidad = viewModel::onAlternarVisibilidad,
        onIniciar = { viewModel.onIniciarRemoto(onInicioExitoso) },
        onLimpiar = viewModel::onLimpiar,
        onCrearCuenta = onCrearCuenta,
        onRecuperarContrasena = onRecuperarContrasena
    )
}

@Composable
fun InicioSesionContent(
    uiState: SesionUiState,
    onCorreoCambia: (String) -> Unit,
    onContrasenaCambia: (String) -> Unit,
    onAlternarVisibilidad: () -> Unit,
    onIniciar: () -> Unit,
    onLimpiar: () -> Unit,
    onCrearCuenta: () -> Unit,
    onRecuperarContrasena: () -> Unit
) {
    val colores = MaterialTheme.colorScheme
    var mostrarContenido by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { mostrarContenido = true }

    val avanceCabecera by animateFloatAsState(
        targetValue = if (mostrarContenido) 1f else 0f,
        animationSpec = tween(380, easing = FastOutSlowInEasing),
        label = "entradaCabeceraLogin"
    )
    val avanceFormulario by animateFloatAsState(
        targetValue = if (mostrarContenido) 1f else 0f,
        animationSpec = tween(420, delayMillis = 55, easing = FastOutSlowInEasing),
        label = "entradaFormularioLogin"
    )
    val avanceAcciones by animateFloatAsState(
        targetValue = if (mostrarContenido) 1f else 0f,
        animationSpec = tween(420, delayMillis = 110, easing = FastOutSlowInEasing),
        label = "entradaAccionesLogin"
    )

    Scaffold(containerColor = colores.background) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 22.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        alpha = avanceCabecera
                        translationY = (1f - avanceCabecera) * 14.dp.toPx()
                    }
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Color(0xFF17236C),
                                Color(0xFF3448D4),
                                Color(0xFF7356CD)
                            )
                        )
                    )
                    .padding(24.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .background(Color.White.copy(alpha = 0.17f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "P",
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "PagoFlex",
                                color = Color.White,
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Text(
                                text = "Tus finanzas, a tu ritmo",
                                color = Color(0xFFDEE5FF),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Organiza. Cumple. Avanza.",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Tus compromisos y pagos, siempre claros y a mano.",
                            color = Color(0xFFE1E7FF),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        alpha = avanceFormulario
                        translationY = (1f - avanceFormulario) * 12.dp.toPx()
                    },
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = colores.surface),
                border = BorderStroke(1.dp, colores.outlineVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Bienvenido de nuevo",
                            style = MaterialTheme.typography.headlineSmall,
                            color = colores.onSurface
                        )
                        Text(
                            text = "Inicia sesión para continuar",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colores.onSurfaceVariant
                        )
                    }

                    CampoTexto(
                        valor = uiState.correo,
                        onValorCambia = onCorreoCambia,
                        etiqueta = "Correo electrónico",
                        icono = Icons.Default.Email,
                        error = uiState.errorCorreo,
                        tipoTeclado = KeyboardType.Email
                    )

                    CampoContrasena(
                        valor = uiState.contrasena,
                        onValorCambia = onContrasenaCambia,
                        etiqueta = "Contraseña",
                        visible = uiState.contrasenaVisible,
                        onAlternarVisible = onAlternarVisibilidad,
                        error = uiState.errorContrasena
                    )

                    if (uiState.errorGeneral != null) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = colores.errorContainer
                        ) {
                            Text(
                                text = uiState.errorGeneral,
                                color = colores.onErrorContainer,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))
                    BotonPrincipal(
                        texto = "Iniciar sesión",
                        onClick = onIniciar,
                        modifier = Modifier.fillMaxWidth()
                    )

                    TextButton(
                        onClick = onRecuperarContrasena,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("¿Olvidaste tu contraseña?")
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        alpha = avanceAcciones
                        translationY = (1f - avanceAcciones) * 10.dp.toPx()
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "¿Todavía no tienes cuenta?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colores.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                BotonSecundario(
                    texto = "Crear cuenta",
                    onClick = onCrearCuenta,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
