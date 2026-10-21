package cl.fintech.pagoflex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.fintech.pagoflex.model.Compromiso
import cl.fintech.pagoflex.model.Usuario
import cl.fintech.pagoflex.ui.components.TarjetaCompromiso
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import cl.fintech.pagoflex.viewmodel.EstadoUf
import java.util.Locale
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.FilterChip
import androidx.compose.runtime.saveable.rememberSaveable

/**
 * Pantalla principal del usuario final: lista de sus compromisos y menu lateral con
 * las opciones "Mi perfil" y "Cerrar sesion".
 *
 * @param nombreEmpresa entrega el nombre comercial a partir del codigo de empresa.
 * @param importeDe entrega el importe de cada compromiso (monto mas recargo, o lo pagado si ya se pago).
 * @param onVerDetalle se llama con el folio del compromiso que se toca.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompromisosScreen(
    usuario: Usuario,
    compromisos: List<Compromiso>,
    estadoUf: EstadoUf,
    nombreEmpresa: (String) -> String,
    importeDe: (Compromiso) -> Int,
    onVerDetalle: (String) -> Unit,
    onVerHistorial: () -> Unit,
    onVerReconocimientos: () -> Unit,
    onVerPerfil: () -> Unit,
    onSalir: () -> Unit

) {
    // Estado del menu lateral (abierto o cerrado) y ambito para abrirlo y cerrarlo
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Indica si se muestra la confirmacion para cerrar sesion
    var mostrarDialogo by remember { mutableStateOf(false) }

    var monedaSeleccionada by rememberSaveable { mutableStateOf("CLP") }

    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("Cerrar sesión") },
            text = { Text("¿Estás seguro que deseas cerrar tu sesión actual?") },
            confirmButton = {
                TextButton(onClick = {
                    mostrarDialogo = false
                    onSalir()
                }) { Text("Cerrar sesión") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false }) { Text("Cancelar") }
            }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        // Contenido del menu lateral
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "Hola, ${usuario.nombreCompleto}",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
                HorizontalDivider()

                NavigationDrawerItem(
                    label = { Text("Historial de pagos") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onVerHistorial()
                    }
                )

                NavigationDrawerItem(
                    label = { Text("Mis reconocimientos") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onVerReconocimientos()
                    }
                )

                NavigationDrawerItem(
                    label = { Text("Mi perfil") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onVerPerfil()
                    }
                )
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
                    title = { Text("Mis compromisos") },
                    navigationIcon = {
                        // Boton que abre el menu lateral
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Abrir menú"
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
            if (compromisos.isEmpty()) {
                // Mensaje centrado cuando la persona no tiene compromisos
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No tienes compromisos registrados")
                }
            } else {
                // Lista que dibuja solo las tarjetas visibles en pantalla
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "Moneda de visualización",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("CLP", "EUR", "USD").forEach { moneda ->
                                    val disponible = when (moneda) {
                                        "EUR" -> (estadoUf.valorEuro ?: 0.0) > 0.0
                                        "USD" -> (estadoUf.valorDolar ?: 0.0) > 0.0
                                        else -> true
                                    }

                                    FilterChip(
                                        selected = monedaSeleccionada == moneda,
                                        onClick = {
                                            monedaSeleccionada = moneda
                                        },
                                        enabled = disponible,
                                        label = { Text(moneda) }
                                    )
                                }
                            }

                            if (monedaSeleccionada != "CLP") {
                                val fecha = when (monedaSeleccionada) {
                                    "EUR" -> estadoUf.fechaEuro
                                    "USD" -> estadoUf.fechaDolar
                                    else -> null
                                }

                                val fechaFormateada = fecha
                                    ?.split("-")
                                    ?.reversed()
                                    ?.joinToString("-") ?: "No disponible"


                                val cotizacionActual = when (monedaSeleccionada) {
                                    "EUR" -> estadoUf.valorEuro
                                    "USD" -> estadoUf.valorDolar
                                    else -> null
                                }

                                if (cotizacionActual != null && cotizacionActual > 0.0) {
                                    val cotizacionFormateada = String.format(
                                        Locale.forLanguageTag("es-CL"),
                                        "%,.2f",
                                        cotizacionActual
                                    )

                                    Text(
                                        "1 $monedaSeleccionada = $cotizacionFormateada CLP",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }


                                Text(
                                    "Conversión aproximada al $fechaFormateada. " +
                                            "Los pagos reales se realizan en CLP. " +
                                            "Fuente: mindicador.cl",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }



                    items(compromisos) { compromiso ->
                        val importe = importeDe(compromiso)

                        val cotizacion = when (monedaSeleccionada) {
                            "EUR" -> estadoUf.valorEuro
                            "USD" -> estadoUf.valorDolar
                            else -> null
                        }

                        val importeConvertido = if (
                            cotizacion != null && cotizacion > 0.0
                        ) {
                            val idioma = if (monedaSeleccionada == "EUR") {
                                Locale.GERMANY
                            } else {
                                Locale.US
                            }

                            val monto = importe.toDouble() / cotizacion

                            "$monedaSeleccionada ${String.format(idioma, "%,.2f", monto)}"
                        } else {
                            null
                        }

                        TarjetaCompromiso(
                            compromiso = compromiso,
                            nombreEmpresa = nombreEmpresa(compromiso.codigoEmpresa),
                            importe = importe,
                            importeConvertido = importeConvertido,
                            onClick = { onVerDetalle(compromiso.folio) }
                        )
                    }

                }
            }
        }
    }
}
