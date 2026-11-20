package cl.fintech.pagoflex.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.fintech.pagoflex.model.RegistroUiState
import cl.fintech.pagoflex.ui.components.BotonPrincipal
import cl.fintech.pagoflex.ui.components.BotonSecundario
import cl.fintech.pagoflex.ui.components.CampoContrasena
import cl.fintech.pagoflex.ui.components.CampoTexto
import cl.fintech.pagoflex.ui.components.ListaDesplegable
import cl.fintech.pagoflex.ui.components.RequisitosContrasena
import cl.fintech.pagoflex.util.Validaciones
import cl.fintech.pagoflex.viewmodel.RegistroViewModel

@Composable
fun RegistroScreen(
    onVolver: () -> Unit,
    onRegistroExitoso: () -> Unit,
    viewModel: RegistroViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    RegistroContent(
        uiState = uiState,
        onNombresCambia = viewModel::onNombresCambia,
        onApellidosCambia = viewModel::onApellidosCambia,
        onRutNumeroCambia = viewModel::onRutNumeroCambia,
        onRutDvCambia = viewModel::onRutDvCambia,
        onCorreoCambia = viewModel::onCorreoCambia,
        onComunaSeleccionada = viewModel::onComunaSeleccionada,
        onContrasenaCambia = viewModel::onContrasenaCambia,
        onRepetirCambia = viewModel::onRepetirCambia,
        onAlternarContrasena = viewModel::onAlternarContrasena,
        onAlternarRepetir = viewModel::onAlternarRepetir,
        onRegistrar = viewModel::onRegistrar,
        onLimpiar = viewModel::onLimpiar,
        onVolver = onVolver,
        onRegistroExitoso = onRegistroExitoso
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroContent(
    uiState: RegistroUiState,
    onNombresCambia: (String) -> Unit,
    onApellidosCambia: (String) -> Unit,
    onRutNumeroCambia: (String) -> Unit,
    onRutDvCambia: (String) -> Unit,
    onCorreoCambia: (String) -> Unit,
    onComunaSeleccionada: (String) -> Unit,
    onContrasenaCambia: (String) -> Unit,
    onRepetirCambia: (String) -> Unit,
    onAlternarContrasena: () -> Unit,
    onAlternarRepetir: () -> Unit,
    onRegistrar: () -> Unit,
    onLimpiar: () -> Unit,
    onVolver: () -> Unit,
    onRegistroExitoso: () -> Unit
) {
    val colores = MaterialTheme.colorScheme

    if (uiState.registroExitoso) {
        AlertDialog(
            onDismissRequest = onRegistroExitoso,
            title = { Text("Cuenta creada") },
            text = {
                Text("¡Bienvenido/a, ${uiState.nombreRegistrado}! Ya puedes iniciar sesión.")
            },
            confirmButton = {
                TextButton(onClick = onRegistroExitoso) {
                    Text("Aceptar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear cuenta") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colores.background,
                    titleContentColor = colores.onBackground,
                    navigationIconContentColor = colores.onBackground
                )
            )
        },
        containerColor = colores.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = colores.surface),
                border = BorderStroke(1.dp, colores.outlineVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(colores.primaryContainer, RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = colores.onPrimaryContainer
                            )
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "Datos personales",
                                style = MaterialTheme.typography.titleLarge,
                                color = colores.onSurface
                            )
                            Text(
                                text = "Tu información para identificar tu cuenta",
                                style = MaterialTheme.typography.bodySmall,
                                color = colores.onSurfaceVariant
                            )
                        }
                    }

                    CampoTexto(
                        valor = uiState.nombres,
                        onValorCambia = onNombresCambia,
                        etiqueta = "Nombres",
                        icono = Icons.Default.Person,
                        error = uiState.errorNombres
                    )

                    CampoTexto(
                        valor = uiState.apellidos,
                        onValorCambia = onApellidosCambia,
                        etiqueta = "Apellidos",
                        icono = Icons.Default.Person,
                        error = uiState.errorApellidos
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        CampoTexto(
                            valor = uiState.rutNumero,
                            onValorCambia = onRutNumeroCambia,
                            etiqueta = "RUT (sin puntos ni guion)",
                            icono = Icons.Default.Badge,
                            error = uiState.errorRut,
                            tipoTeclado = KeyboardType.Number,
                            modifier = Modifier.weight(2.2f)
                        )

                        CampoTexto(
                            valor = uiState.rutDv,
                            onValorCambia = onRutDvCambia,
                            etiqueta = "DV",
                            icono = Icons.Default.Check,
                            error = uiState.errorRut,
                            mostrarMensajeError = false,
                            capitalizacion = KeyboardCapitalization.Characters,
                            modifier = Modifier.weight(1f)
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

                    ListaDesplegable(
                        etiqueta = "Comuna",
                        opciones = uiState.comunas,
                        seleccionada = uiState.comuna.ifEmpty { null },
                        textoDe = { it },
                        onSeleccionar = onComunaSeleccionada,
                        error = uiState.errorComuna
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = colores.surface),
                border = BorderStroke(1.dp, colores.outlineVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(colores.secondaryContainer, RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = colores.onSecondaryContainer
                            )
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "Seguridad de tu cuenta",
                                style = MaterialTheme.typography.titleLarge,
                                color = colores.onSurface
                            )
                            Text(
                                text = "Define una contraseña segura",
                                style = MaterialTheme.typography.bodySmall,
                                color = colores.onSurfaceVariant
                            )
                        }
                    }

                    CampoContrasena(
                        valor = uiState.contrasena,
                        onValorCambia = onContrasenaCambia,
                        etiqueta = "Contraseña",
                        visible = uiState.contrasenaVisible,
                        onAlternarVisible = onAlternarContrasena,
                        error = uiState.errorContrasena
                    )

                    RequisitosContrasena(
                        requisitos = Validaciones.requisitosContrasena(
                            contrasena = uiState.contrasena,
                            correo = uiState.correo,
                            nombres = "${uiState.nombres} ${uiState.apellidos}"
                        )
                    )

                    CampoContrasena(
                        valor = uiState.repetirContrasena,
                        onValorCambia = onRepetirCambia,
                        etiqueta = "Repetir contraseña",
                        visible = uiState.repetirVisible,
                        onAlternarVisible = onAlternarRepetir,
                        error = uiState.errorRepetir
                    )
                }
            }

            if (uiState.errorGeneral != null) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = colores.errorContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = uiState.errorGeneral,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colores.onErrorContainer,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }

            BotonPrincipal(
                texto = if (uiState.registrando) "Registrando..." else "Crear mi cuenta",
                onClick = onRegistrar,
                habilitado = !uiState.registrando,
                icono = Icons.Default.Check,
                modifier = Modifier.fillMaxWidth()
            )

            BotonSecundario(
                texto = "Limpiar campos",
                onClick = onLimpiar,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}
