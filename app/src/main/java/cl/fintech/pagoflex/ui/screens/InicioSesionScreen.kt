
package cl.fintech.pagoflex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.app.pagoflex.viewmodel.InicioSesionViewModel
import cl.fintech.pagoflex.model.Rol
import cl.fintech.pagoflex.model.SesionUiState
import cl.fintech.pagoflex.model.Usuario

@Composable
fun InicioSesionScreen(
    onInicioExitoso: (Usuario) -> Unit,
    viewModel: InicioSesionViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    InicioSesionContent(
        uiState = uiState,
        onCorreoCambia = viewModel::onCorreoCambia,
        onContrasenaCambia = viewModel::onContrasenaCambia,
        onAlternarVisibilidad = viewModel::onAlternarVisibilidad,
        onRolSeleccionado = viewModel::onRolSeleccionado,
        onIniciar = {
            viewModel.onIniciar()?.let { usuario ->
                onInicioExitoso(usuario)
            }
        },
        onLimpiar = viewModel::onLimpiar
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioSesionContent(
    uiState: SesionUiState,
    onCorreoCambia: (String) -> Unit,
    onContrasenaCambia: (String) -> Unit,
    onAlternarVisibilidad: () -> Unit,
    onRolSeleccionado: (Rol) -> Unit,
    onIniciar: () -> Unit,
    onLimpiar: () -> Unit
) {
    var rolExpandido by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "PagoFlex",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Inicio de sesión",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Ingresa tus credenciales para continuar",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(28.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Correo electrónico

                    OutlinedTextField(
                        value = uiState.correo,
                        onValueChange = onCorreoCambia,
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text("Correo electrónico")
                        },
                        placeholder = {
                            Text("ejemplo@correo.cl")
                        },
                        singleLine = true,
                        isError = uiState.errorCorreo != null,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        ),
                        supportingText = {
                            uiState.errorCorreo?.let { error ->
                                Text(
                                    text = error,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    )

                    // Contraseña

                    OutlinedTextField(
                        value = uiState.contrasena,
                        onValueChange = onContrasenaCambia,
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text("Contraseña")
                        },
                        singleLine = true,
                        isError = uiState.errorContrasena != null,
                        visualTransformation = if (
                            uiState.contrasenaVisible
                        ) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        trailingIcon = {
                            TextButton(
                                onClick = onAlternarVisibilidad
                            ) {
                                Text(
                                    if (uiState.contrasenaVisible) {
                                        "Ocultar"
                                    } else {
                                        "Mostrar"
                                    }
                                )
                            }
                        },
                        supportingText = {
                            uiState.errorContrasena?.let { error ->
                                Text(
                                    text = error,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    )

                    // Selector de rol

                    ExposedDropdownMenuBox(
                        expanded = rolExpandido,
                        onExpandedChange = {
                            rolExpandido = it
                        }
                    ) {
                        OutlinedTextField(
                            value = uiState.rol.name
                                .replace("_", " "),
                            onValueChange = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(
                                    ExposedDropdownMenuAnchorType
                                        .PrimaryNotEditable
                                ),
                            readOnly = true,
                            label = {
                                Text("Tipo de usuario")
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults
                                    .TrailingIcon(
                                        expanded = rolExpandido
                                    )
                            },
                            singleLine = true
                        )

                        ExposedDropdownMenu(
                            expanded = rolExpandido,
                            onDismissRequest = {
                                rolExpandido = false
                            }
                        ) {
                            Rol.values().forEach { rol ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            rol.name.replace("_", " ")
                                        )
                                    },
                                    onClick = {
                                        onRolSeleccionado(rol)
                                        rolExpandido = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Botones

                    Button(
                        onClick = onIniciar,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Iniciar sesión")
                    }

                    OutlinedButton(
                        onClick = onLimpiar,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Limpiar campos")
                    }

                    // Mensaje de inicio exitoso

                    if (uiState.inicioExitoso) {
                        Text(
                            text = "Inicio de sesión exitoso",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
