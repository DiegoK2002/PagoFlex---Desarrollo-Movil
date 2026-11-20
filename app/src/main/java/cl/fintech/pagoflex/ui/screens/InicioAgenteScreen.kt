package cl.fintech.pagoflex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.fintech.pagoflex.model.Usuario
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import cl.fintech.pagoflex.model.Reporte
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioAgenteScreen(
    usuario: Usuario,
    onSalir: () -> Unit,
    reportes: List<Reporte> = emptyList(),
    operandoReporte: Boolean = false,
    errorReporte: String? = null,
    cargandoReportes: Boolean = false,
    onActualizar: () -> Unit,
    onTomarReporte: (Long) -> Unit,
    onResolverReporte: (Long, String, (Boolean) -> Unit) -> Unit
) {
    var filtroEstado by rememberSaveable { mutableStateOf("Por resolver") }
    var textoBusqueda by rememberSaveable { mutableStateOf("") }

    val reportesPorEstado = when (filtroEstado) {
        "Por resolver" -> reportes.filter { it.estado == "Abierto" || it.estado == "En gestión" }
        "Abiertos" -> reportes.filter { it.estado == "Abierto" }
        "En gestión" -> reportes.filter { it.estado == "En gestión" }
        "Resueltos" -> reportes.filter { it.estado == "Resuelto" }
        else -> reportes
    }

    val busqueda = textoBusqueda.trim()
    val reportesVisibles = reportesPorEstado.filter { reporte ->
        busqueda.isBlank() ||
                reporte.idReporte.toString().contains(busqueda, ignoreCase = true) ||
                reporte.folio.contains(busqueda, ignoreCase = true) ||
                reporte.motivo.contains(busqueda, ignoreCase = true) ||
                reporte.comentario?.contains(busqueda, ignoreCase = true) == true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reportes") },
                actions = {
                    TextButton(
                        onClick = onSalir,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) { Text("Salir") }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = cargandoReportes,
            onRefresh = onActualizar,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(usuario.nombreCompleto, style = MaterialTheme.typography.headlineSmall)
                        Text(usuario.rol.etiqueta, style = MaterialTheme.typography.bodyLarge)

                        if (errorReporte != null) {
                            Text(errorReporte, color = MaterialTheme.colorScheme.error)
                        }

                        Text("Filtrar reportes", style = MaterialTheme.typography.titleMedium)

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Por resolver", "Abiertos", "En gestión", "Resueltos", "Todos").forEach { filtro ->
                                FilterChip(
                                    selected = filtroEstado == filtro,
                                    onClick = { filtroEstado = filtro },
                                    label = { Text(filtro) }
                                )
                            }
                        }

                        OutlinedTextField(
                            value = textoBusqueda,
                            onValueChange = { textoBusqueda = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Buscar reportes") },
                            placeholder = { Text("N.º de reporte, compromiso o motivo") },
                            singleLine = true
                        )
                    }
                }

                if (reportesVisibles.isEmpty()) {
                    item {
                        Text(
                            when {
                                cargandoReportes -> "Cargando reportes..."
                                reportes.isEmpty() -> "No hay reportes para mostrar."
                                busqueda.isNotBlank() -> "No se encontraron reportes con esa búsqueda."
                                else -> "No hay reportes para el filtro seleccionado."
                            },
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    items(reportesVisibles, key = { it.idReporte }) { reporte ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Reporte #${reporte.idReporte}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text("Compromiso: ${reporte.folio}")
                                Text("Motivo: ${reporte.motivo}")

                                if (!reporte.comentario.isNullOrBlank()) {
                                    Text("Descripción: ${reporte.comentario}")
                                } else {
                                    Text("Descripción: Sin comentario adicional")
                                }

                                Text("Estado: ${reporte.estado}")

                                if (reporte.estado == "Abierto") {
                                    Button(
                                        onClick = { onTomarReporte(reporte.idReporte) },
                                        enabled = !operandoReporte
                                    ) {
                                        Text("Tomar reporte")
                                    }
                                }

                                if (reporte.estado == "En gestión" && reporte.idAgente == usuario.id) {
                                    var respuesta by rememberSaveable { mutableStateOf("") }
                                    var mostrarError by rememberSaveable { mutableStateOf(false) }

                                    OutlinedTextField(
                                        value = respuesta,
                                        onValueChange = {
                                            respuesta = it
                                            mostrarError = false
                                        },
                                        label = { Text("Respuesta al usuario") },
                                        modifier = Modifier.fillMaxWidth(),
                                        minLines = 3
                                    )

                                    Button(
                                        onClick = {
                                            onResolverReporte(reporte.idReporte, respuesta) { correcto ->
                                                mostrarError = !correcto
                                            }
                                        },
                                        enabled = respuesta.isNotBlank() && !operandoReporte
                                    ) {
                                        Text("Resolver reporte")
                                    }

                                    if (mostrarError) {
                                        Text(
                                            "No se pudo resolver el reporte.",
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }

                                if (!reporte.respuesta.isNullOrBlank()) {
                                    Text("Respuesta: ${reporte.respuesta}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
