package cl.app.pagoflex.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cl.app.pagoflex.R
import cl.app.pagoflex.model.Compromiso
import cl.app.pagoflex.model.Usuario
import cl.app.pagoflex.ui.components.TarjetaCompromiso
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioUsuarioScreen(
    usuario: Usuario,
    compromisos: List<Compromiso>,
    nombreEmpresa: (String) -> String,
    onVerDetalle: (String) -> Unit,
    onSalir: () -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var mostrarDialogo by remember { mutableStateOf(false) }

    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("Cerrar Sesión") },
            text = { Text("¿Estás seguro que deseas cerrar tu sesión actual?") },
            confirmButton = {
                TextButton(onClick = {
                    mostrarDialogo = false
                    onSalir()
                }) { Text("Cerrar sesión") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "Hola, ${usuario.nombreCompleto}",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
                HorizontalDivider()

                NavigationDrawerItem(
                    label = { Text("Cerrar sesión") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        mostrarDialogo = true
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Mis Compromisos") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                painter = painterResource(android.R.drawable.ic_menu_sort_by_size),
                                contentDescription = "Abrir Menú"
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(compromisos) { compromiso ->
                    TarjetaCompromiso(
                        compromiso = compromiso,
                        nombreEmpresa = nombreEmpresa(compromiso.codigoEmpresa),
                        onClick = { onVerDetalle(compromiso.folio) }
                    )
                }
            }
        }
    }
}