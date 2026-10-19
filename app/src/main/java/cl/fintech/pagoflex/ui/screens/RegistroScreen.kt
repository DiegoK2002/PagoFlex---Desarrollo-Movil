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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import cl.fintech.pagoflex.model.Rol
import cl.fintech.pagoflex.ui.components.BotonPrincipal
import cl.fintech.pagoflex.ui.components.BotonSecundario
import cl.fintech.pagoflex.ui.components.CampoContrasena
import cl.fintech.pagoflex.ui.components.CampoTexto
import cl.fintech.pagoflex.ui.components.ListaDesplegable
import cl.fintech.pagoflex.ui.components.RequisitosContrasena
import cl.fintech.pagoflex.util.Validaciones
import cl.fintech.pagoflex.viewmodel.RegistroViewModel

/**
 * Pantalla para crear una cuenta. Conecta el [RegistroViewModel] con la interfaz.
 *
 * @param onVolver se llama al tocar la flecha de volver.
 * @param onRegistroExitoso se llama cuando la persona acepta el aviso de cuenta creada.
 */
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
        onRolSeleccionado = viewModel::onRolSeleccionado,
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

/** Dibuja el formulario de registro a partir del estado recibido, sin logica propia. */
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
    onRolSeleccionado: (Rol) -> Unit,
    onContrasenaCambia: (String) -> Unit,
    onRepetirCambia: (String) -> Unit,
    onAlternarContrasena: () -> Unit,
    onAlternarRepetir: () -> Unit,
    onRegistrar: () -> Unit,
    onLimpiar: () -> Unit,
    onVolver: () -> Unit,
    onRegistroExitoso: () -> Unit
) {
    // Aviso de registro exitoso: al aceptarlo se vuelve al inicio de sesion
    if (uiState.registroExitoso) {
        AlertDialog(
            onDismissRequest = onRegistroExitoso,
            title = { Text("Cuenta creada") },
            text = { Text("¡Bienvenido/a, ${uiState.nombreRegistrado}! Ya puedes iniciar sesión.") },
            confirmButton = {
                TextButton(onClick = onRegistroExitoso) { Text("Aceptar") }
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
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // ----- Datos personales -----
            Text(
                text = "Datos personales",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

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

            // RUT en dos campos: el numero usa teclado numerico (solo digitos) y el digito verificador
            // admite un digito o la letra K
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                CampoTexto(
                    valor = uiState.rutNumero,
                    onValorCambia = onRutNumeroCambia,
                    etiqueta = "RUT (sin puntos ni guión)",
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
                    mostrarMensajeError = false,   // el mensaje ya se muestra bajo el campo del RUT
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
            ListaDesplegable(
                etiqueta = "Tipo de usuario",
                opciones = Rol.entries.toList(),
                seleccionada = uiState.rol,
                textoDe = { it.etiqueta },
                onSeleccionar = onRolSeleccionado
            )

            // ----- Seguridad -----
            Text(
                text = "Seguridad",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            CampoContrasena(
                valor = uiState.contrasena,
                onValorCambia = onContrasenaCambia,
                etiqueta = "Contrase\u00f1a",
                visible = uiState.contrasenaVisible,
                onAlternarVisible = onAlternarContrasena,
                error = uiState.errorContrasena
            )

            // Lista de condiciones que se va marcando a medida que se escribe la contrasena
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
                etiqueta = "Repetir contrase\u00f1a",
                visible = uiState.repetirVisible,
                onAlternarVisible = onAlternarRepetir,
                error = uiState.errorRepetir
            )

            // ----- Acciones -----
            BotonPrincipal(
                texto = "Registrarme",
                onClick = onRegistrar,
                icono = Icons.Default.Check,
                modifier = Modifier.fillMaxWidth()
            )
            BotonSecundario(
                texto = "Limpiar",
                onClick = onLimpiar,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
