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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.fintech.pagoflex.util.Reconocimiento
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReconocimientosScreen(
    reconocimientos: List<Reconocimiento>,
    onVolver: () -> Unit
) {
    val formatoFecha = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    var pestanaSeleccionada by rememberSaveable { mutableIntStateOf(0) }
    val esConstancia = pestanaSeleccionada == 1
    val unidad = if (esConstancia) "meses consecutivos" else "pagos a tiempo"
    val reconocimientosVisibles = reconocimientos.filter { it.unidad == unidad }
    val obtenidosVisibles = reconocimientosVisibles.count { it.obtenido }
    val siguiente = reconocimientosVisibles
        .filter { !it.obtenido }
        .minByOrNull { it.faltantes }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis reconocimientos") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TabRow(selectedTabIndex = pestanaSeleccionada) {
                Tab(
                    selected = pestanaSeleccionada == 0,
                    onClick = { pestanaSeleccionada = 0 },
                    text = { Text("Pagos a tiempo") }
                )
                Tab(
                    selected = pestanaSeleccionada == 1,
                    onClick = { pestanaSeleccionada = 1 },
                    text = { Text("Constancia") }
                )
            }

            key(pestanaSeleccionada) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                "Tus avances cuentan",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Text(
                                if (esConstancia) {
                                    "Cada mes con pagos a tiempo te acerca a una nueva meta de constancia."
                                } else {
                                    "Cada pago realizado a tiempo te acerca a una nueva meta."
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (reconocimientosVisibles.isEmpty()) {
                        item {
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    "Todavía no hay reconocimientos para mostrar. Actualiza tus compromisos e inténtalo nuevamente.",
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    } else {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Text(
                                                if (esConstancia) "Tu constancia" else "Tus logros por pagos",
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                            Text(
                                                "$obtenidosVisibles de ${reconocimientosVisibles.size} metas alcanzadas",
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                        }
                                        Icon(
                                            imageVector = Icons.Filled.EmojiEvents,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    LinearProgressIndicator(
                                        progress = {
                                            obtenidosVisibles.toFloat() / reconocimientosVisibles.size.toFloat()
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }

                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        if (siguiente != null) {
                                            if (esConstancia) "Tu próxima meta de constancia" else "Próximo logro por pagos"
                                        } else {
                                            if (esConstancia) "¡Metas de constancia alcanzadas!" else "¡Metas de pagos alcanzadas!"
                                        },
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                    if (siguiente != null) {
                                        Text(
                                            siguiente.titulo,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        val meta = siguiente.meta.takeIf { it > 0 }
                                        if (meta != null) {
                                            val progreso = (meta - siguiente.faltantes)
                                                .coerceIn(0, meta)
                                            LinearProgressIndicator(
                                                progress = { progreso.toFloat() / meta.toFloat() },
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                            Text(
                                                "$progreso de $meta ${siguiente.unidad}",
                                                style = MaterialTheme.typography.labelLarge
                                            )
                                        }
                                        Text(
                                            mensajeFaltantes(siguiente),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    } else {
                                        Text(
                                            if (esConstancia) {
                                                "Ya conseguiste los reconocimientos de dos y tres meses consecutivos."
                                            } else {
                                                "Completaste todas las metas de pagos a tiempo. Sigue cuidando tus fechas de pago."
                                            },
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    if (esConstancia) "Logros por constancia mensual" else "Logros por pagos a tiempo",
                                    style = MaterialTheme.typography.titleLarge
                                )
                                if (esConstancia) {
                                    Text(
                                        "Un mes cuenta cuando realizas al menos un pago a tiempo. Para conseguir estas metas, los meses deben ser consecutivos.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        items(reconocimientosVisibles, key = { it.titulo }) { reconocimiento ->
                            val meta = reconocimiento.meta.takeIf { it > 0 }
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (reconocimiento.obtenido) {
                                        MaterialTheme.colorScheme.primaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant
                                    }
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = if (reconocimiento.obtenido) {
                                                Icons.Filled.EmojiEvents
                                            } else {
                                                Icons.Filled.Flag
                                            },
                                            contentDescription = null,
                                            tint = if (reconocimiento.obtenido) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                MaterialTheme.colorScheme.onSurfaceVariant
                                            }
                                        )
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                reconocimiento.titulo,
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                            Text(
                                                if (reconocimiento.obtenido) "Conseguido" else "En progreso",
                                                style = MaterialTheme.typography.labelMedium
                                            )
                                        }
                                    }
                                    Text(
                                        reconocimiento.descripcion,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    if (meta != null) {
                                        val progreso = (meta - reconocimiento.faltantes)
                                            .coerceIn(0, meta)
                                        LinearProgressIndicator(
                                            progress = { progreso.toFloat() / meta.toFloat() },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        Text(
                                            "$progreso / $meta ${reconocimiento.unidad}",
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                    }
                                    if (reconocimiento.obtenido) {
                                        reconocimiento.fechaObtenida?.let { fecha ->
                                            Text(
                                                "Conseguido el ${fecha.format(formatoFecha)}",
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    } else {
                                        Text(
                                            mensajeFaltantes(reconocimiento),
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            Text(
                                "Las metas se calculan a partir de pagos confirmados realizados dentro del plazo. No modifican tus compromisos ni los montos que debes pagar.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun mensajeFaltantes(reconocimiento: Reconocimiento): String =
    if (reconocimiento.unidad == "meses consecutivos") {
        if (reconocimiento.faltantes == 1) {
            "Te falta 1 mes consecutivo con un pago a tiempo."
        } else {
            "Te faltan ${reconocimiento.faltantes} meses consecutivos con pagos a tiempo."
        }
    } else if (reconocimiento.faltantes == 1) {
        "Te falta 1 pago a tiempo para alcanzarla."
    } else {
        "Te faltan ${reconocimiento.faltantes} pagos a tiempo para alcanzarla."
    }
