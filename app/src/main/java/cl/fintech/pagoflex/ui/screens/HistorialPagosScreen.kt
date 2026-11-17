package cl.fintech.pagoflex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.fintech.pagoflex.data.remote.PagoRemoto
import cl.fintech.pagoflex.model.Compromiso
import cl.fintech.pagoflex.ui.components.ListaDesplegable
import cl.fintech.pagoflex.ui.components.BotonSecundario
import java.text.NumberFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialPagosScreen(
    pagos: List<PagoRemoto>,
    compromisos: List<Compromiso>,
    nombreEmpresa: (String) -> String,
    cargando: Boolean,
    error: String?,
    onActualizar: () -> Unit,
    onVerComprobante: (String) -> Unit,
    onVolver: () -> Unit
) {
    var empresaSeleccionada by rememberSaveable { mutableStateOf("") }
    var periodoSeleccionado by rememberSaveable { mutableStateOf("Todo el historial") }

    val compromisosPorId = compromisos.mapNotNull { compromiso ->
        compromiso.folio.removePrefix("REM-").toLongOrNull()?.let { id ->
            id to compromiso
        }
    }.toMap()

    val opcionesPeriodos = listOf(
        "Todo el historial",
        "Este mes",
        "Mes anterior",
        "Últimos 12 meses"
    )

    val opcionesEmpresas = listOf("") + pagos.mapNotNull { pago ->
        compromisosPorId[pago.idCompromiso]?.codigoEmpresa
    }.distinct().sortedBy(nombreEmpresa)

    val hoy = LocalDate.now()
    val mesActual = YearMonth.from(hoy)
    val mesAnterior = mesActual.minusMonths(1)

    val pagosFiltrados = pagos.filter { pago ->
        val fecha = runCatching { LocalDate.parse(pago.fechaPago) }.getOrNull()
        val empresa = compromisosPorId[pago.idCompromiso]?.codigoEmpresa
        val coincideEmpresa = empresaSeleccionada.isEmpty() || empresa == empresaSeleccionada
        val coincidePeriodo = when (periodoSeleccionado) {
            "Este mes" -> fecha != null && YearMonth.from(fecha) == mesActual
            "Mes anterior" -> fecha != null && YearMonth.from(fecha) == mesAnterior
            "Últimos 12 meses" -> fecha != null &&
                !fecha.isBefore(hoy.minusMonths(12)) && !fecha.isAfter(hoy)
            else -> true
        }
        coincideEmpresa && coincidePeriodo
    }

    val numeroFormato = NumberFormat.getNumberInstance(Locale.forLanguageTag("es-CL")).apply {
        minimumFractionDigits = 0
        maximumFractionDigits = 2
    }
    val fechaFormato = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de pagos") },
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
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("Resumen de pagos", style = MaterialTheme.typography.titleMedium)
                        Text("Pagos registrados: ${pagos.size}")
                        Text("Pagos confirmados: ${pagos.count { it.estadoSincronizacion == "CONFIRMADO" }}")
                    }
                }
            }

            if (cargando) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else if (error != null) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(error, color = MaterialTheme.colorScheme.error)
                        Button(onClick = onActualizar) { Text("Reintentar") }
                    }
                }
            } else {
                item {
                    ListaDesplegable(
                        etiqueta = "Filtrar por empresa",
                        opciones = opcionesEmpresas,
                        seleccionada = empresaSeleccionada,
                        textoDe = { codigo ->
                            if (codigo.isEmpty()) "Todas las empresas" else nombreEmpresa(codigo)
                        },
                        onSeleccionar = { empresaSeleccionada = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    ListaDesplegable(
                        etiqueta = "Filtrar por período",
                        opciones = opcionesPeriodos,
                        seleccionada = periodoSeleccionado,
                        textoDe = { it },
                        onSeleccionar = { periodoSeleccionado = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (pagos.isEmpty()) {
                    item { Text("Todavía no tienes pagos registrados en el servidor.") }
                } else if (pagosFiltrados.isEmpty()) {
                    item {
                        Text(
                            "No se encontraron pagos para los filtros seleccionados.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                items(pagosFiltrados, key = { it.idPago }) { pago ->
                    val compromiso = compromisosPorId[pago.idCompromiso]
                    val fecha = runCatching { LocalDate.parse(pago.fechaPago) }.getOrNull()
                    val estado = when (pago.estadoSincronizacion?.uppercase()) {
                        "CONFIRMADO" -> "Confirmado"
                        "PENDIENTE" -> "Pendiente de confirmación"
                        else -> "Estado de confirmación no disponible"
                    }

                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                compromiso?.let { nombreEmpresa(it.codigoEmpresa) }
                                    ?: "Empresa no disponible",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(compromiso?.concepto ?: "Compromiso #${pago.idCompromiso}")
                            Text("Pago #${pago.idPago}")
                            Text("Fecha: ${fecha?.format(fechaFormato) ?: pago.fechaPago}")
                            Text(
                                "Monto pagado: $${numeroFormato.format(pago.monto)}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text("Método: ${pago.metodoPago}")
                            Text(
                                "Estado: $estado",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            pago.referenciaComprobante?.takeIf { it.isNotBlank() }?.let {
                                Text("Referencia: $it")
                            }
                            BotonSecundario(
                                texto = "Ver comprobante",
                                onClick = {
                                    onVerComprobante("REM-${pago.idCompromiso}")
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}
