
package cl.fintech.pagoflex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.fintech.pagoflex.model.Pago
import cl.fintech.pagoflex.util.formatearFechaHora
import cl.fintech.pagoflex.util.formatearPesos
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import cl.fintech.pagoflex.ui.components.ListaDesplegable
import java.time.LocalDate
import java.time.YearMonth
import cl.fintech.pagoflex.util.IndicadorCumplimiento
import cl.fintech.pagoflex.ui.components.GraficoCumplimiento

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialPagosScreen(
    pagos: List<Pago>,
    nombreEmpresa: (String) -> String,
    onVerComprobante: (String) -> Unit,
    onVolver: () -> Unit,
    indicador: IndicadorCumplimiento = IndicadorCumplimiento(0, 0)
) {

    var empresaSeleccionada by rememberSaveable {
        mutableStateOf("")
    }

    var periodoSeleccionado by rememberSaveable {
        mutableStateOf("Todo el historial")
    }

    val opcionesPeriodos = listOf(
        "Todo el historial",
        "Este mes",
        "Mes anterior",
        "Últimos 12 meses"
    )

    val opcionesEmpresas = listOf("") +
            pagos.map { it.codigoEmpresa }
                .distinct()
                .sortedBy { nombreEmpresa(it) }


    val hoy = LocalDate.now()
    val mesActual = YearMonth.from(hoy)
    val mesAnterior = mesActual.minusMonths(1)

    val pagosFiltrados = pagos.filter { pago ->
        val fecha = pago.fechaHora.toLocalDate()

        val coincideEmpresa =
            empresaSeleccionada.isEmpty() ||
                    pago.codigoEmpresa == empresaSeleccionada

        val coincidePeriodo = when (periodoSeleccionado) {
            "Este mes" -> YearMonth.from(fecha) == mesActual

            "Mes anterior" -> YearMonth.from(fecha) == mesAnterior

            "Últimos 12 meses" ->
                !fecha.isBefore(hoy.minusMonths(12)) &&
                        !fecha.isAfter(hoy)

            else -> true
        }

        coincideEmpresa && coincidePeriodo
    }


    Scaffold(
        topBar = {
            Column {
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

                val porcentaje = indicador.porcentaje

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            "Cumplimiento de los últimos 12 meses",
                            style = MaterialTheme.typography.titleMedium
                        )


                        if (porcentaje == null) {
                            Text(
                                "Historial insuficiente",
                                style = MaterialTheme.typography.titleLarge
                            )

                            Text(
                                "Se necesitan al menos 3 compromisos evaluables."
                            )
                        } else {
                            GraficoCumplimiento(
                                porcentaje = porcentaje,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )

                            Text(
                                indicador.categoria ?: "",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                "${indicador.pagadosATiempo} de ${indicador.evaluados} compromisos pagados a tiempo"
                            )
                        }

                    }
                }
            }
            }
    ) { innerPadding ->
        if (pagos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Todavía no tienes pagos registrados.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                item {
                    ListaDesplegable(
                        etiqueta = "Filtrar por empresa",
                        opciones = opcionesEmpresas,
                        seleccionada = empresaSeleccionada,
                        textoDe = { codigo ->
                            if (codigo.isEmpty()) {
                                "Todas las empresas"
                            } else {
                                nombreEmpresa(codigo)
                            }
                        },
                        onSeleccionar = { codigo ->
                            empresaSeleccionada = codigo
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    ListaDesplegable(
                        etiqueta = "Filtrar por período",
                        opciones = opcionesPeriodos,
                        seleccionada = periodoSeleccionado,
                        textoDe = { it },
                        onSeleccionar = { periodo ->
                            periodoSeleccionado = periodo
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (pagosFiltrados.isEmpty()) {
                    item {
                        Text(
                            "No se encontraron pagos para los filtros seleccionados.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                items(pagosFiltrados, key = { it.numeroComprobante }) { pago ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                nombreEmpresa(pago.codigoEmpresa),
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(pago.concepto)

                            Text(
                                "Fecha: ${formatearFechaHora(pago.fechaHora)}"
                            )

                            Text(
                                "Monto pagado: ${formatearPesos(pago.montoPagado)}",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text("Canal: ${pago.canal}")

                            Text(
                                if (pago.aTiempo) {
                                    "Pagado a tiempo"
                                } else {
                                    "Pagado con atraso"
                                },
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            TextButton(
                                onClick = {
                                    onVerComprobante(pago.folioCompromiso)
                                }
                            ) {
                                Text("Ver comprobante")
                            }
                        }
                    }
                }
            }
        }
    }
}
