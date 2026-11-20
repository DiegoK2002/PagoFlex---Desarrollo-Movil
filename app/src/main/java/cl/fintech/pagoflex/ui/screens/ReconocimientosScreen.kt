package cl.fintech.pagoflex.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
    val modoOscuro = isSystemInDarkTheme()
    val coloresCabecera = if (modoOscuro) {
        listOf(Color(0xFF263A91), Color(0xFF543184))
    } else {
        listOf(Color(0xFF3448D4), Color(0xFF7545BA))
    }

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

            Crossfade(
                targetState = pestanaSeleccionada,
                modifier = Modifier.fillMaxSize(),
                animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing),
                label = "Transición de reconocimientos"
            ) { pestanaActiva ->
                val esConstancia = pestanaActiva == 1
                val unidad = if (esConstancia) "meses consecutivos" else "pagos a tiempo"
                val reconocimientosVisibles = reconocimientos.filter { it.unidad == unidad }
                val obtenidosVisibles = reconocimientosVisibles.count { it.obtenido }
                val siguiente = reconocimientosVisibles
                    .filter { !it.obtenido }
                    .minByOrNull { it.faltantes }

                val progresoGlobal = if (reconocimientosVisibles.isEmpty()) 0f
                else obtenidosVisibles.toFloat() / reconocimientosVisibles.size
                val animacionGlobal = remember(pestanaActiva) { Animatable(0f) }
                LaunchedEffect(pestanaActiva, progresoGlobal) {
                    animacionGlobal.animateTo(
                        targetValue = progresoGlobal,
                        animationSpec = tween(650, easing = FastOutSlowInEasing)
                    )
                }

                val progresoMeta = if (siguiente != null && siguiente.meta > 0) {
                    (siguiente.meta - siguiente.faltantes).coerceIn(0, siguiente.meta)
                        .toFloat() / siguiente.meta
                } else {
                    0f
                }
                val animacionMeta = remember(pestanaActiva) { Animatable(0f) }
                LaunchedEffect(pestanaActiva, progresoMeta) {
                    animacionMeta.animateTo(
                        targetValue = progresoMeta,
                        animationSpec = tween(700, easing = FastOutSlowInEasing)
                    )
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(26.dp))
                                .background(Brush.linearGradient(coloresCabecera))
                        ) {
                            Column(
                                modifier = Modifier.padding(22.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        if (esConstancia) "TU CONSTANCIA" else "TUS LOGROS",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = Color(0xFFE4E8FF),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = Color.White.copy(alpha = 0.17f)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.EmojiEvents,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.padding(10.dp)
                                        )
                                    }
                                }
                                Row(
                                    verticalAlignment = Alignment.Bottom,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        "$obtenidosVisibles / ${reconocimientosVisibles.size}",
                                        style = MaterialTheme.typography.displaySmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "metas alcanzadas",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFFE4E8FF),
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )
                                }
                                LinearProgressIndicator(
                                    progress = { animacionGlobal.value },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(50)),
                                    color = Color.White,
                                    trackColor = Color.White.copy(alpha = 0.23f)
                                )
                                Text(
                                    if (esConstancia) {
                                        "Cada mes cumplido suma a tu trayectoria."
                                    } else {
                                        "Cada compromiso pagado a tiempo cuenta."
                                    },
                                    color = Color(0xFFE4E8FF),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    if (reconocimientosVisibles.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(24.dp)
                            ) {
                                Text(
                                    "Todavía no hay reconocimientos para mostrar. Actualiza tus compromisos e inténtalo nuevamente.",
                                    modifier = Modifier.padding(20.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    } else {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.outlineVariant
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(14.dp),
                                            color = MaterialTheme.colorScheme.secondaryContainer
                                        ) {
                                            Icon(
                                                imageVector = if (siguiente != null) {
                                                    Icons.Filled.Flag
                                                } else {
                                                    Icons.Filled.CheckCircle
                                                },
                                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                                contentDescription = null,
                                                modifier = Modifier.padding(12.dp)
                                            )
                                        }
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                if (siguiente != null) "SIGUIENTE META" else "TODAS COMPLETADAS",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = MaterialTheme.colorScheme.secondary,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                siguiente?.titulo ?: "¡Conseguiste todos los logros!",
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                    if (siguiente != null) {
                                        val meta = siguiente.meta.takeIf { it > 0 }
                                        if (meta != null) {
                                            val progreso = (meta - siguiente.faltantes).coerceIn(0, meta)
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    "Tu progreso",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                Text(
                                                    "$progreso / $meta",
                                                    style = MaterialTheme.typography.labelLarge,
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            LinearProgressIndicator(
                                                progress = { animacionMeta.value },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(8.dp)
                                                    .clip(RoundedCornerShape(50)),
                                                color = MaterialTheme.colorScheme.secondary,
                                                trackColor = MaterialTheme.colorScheme.secondaryContainer
                                            )
                                        }
                                        Text(
                                            mensajeFaltantes(siguiente),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    } else {
                                        Text(
                                            if (esConstancia) {
                                                "Ya conseguiste los reconocimientos de dos y tres meses consecutivos."
                                            } else {
                                                "Completaste todas las metas de pagos a tiempo. Sigue cuidando tus fechas de pago."
                                            },
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                Text(
                                    if (esConstancia) "Constancia mensual" else "Colección de logros",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    if (esConstancia) {
                                        "Para obtener estas metas necesitas pagos a tiempo en meses consecutivos."
                                    } else {
                                        "Reúne tus reconocimientos con cada pago a tiempo."
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        items(reconocimientosVisibles, key = { it.titulo }) { reconocimiento ->
                            val meta = reconocimiento.meta.takeIf { it > 0 }
                            val conseguido = reconocimiento.obtenido
                            val colorLogro = if (conseguido) {
                                MaterialTheme.colorScheme.secondary
                            } else {
                                MaterialTheme.colorScheme.primary
                            }
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    if (conseguido) {
                                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f)
                                    } else {
                                        MaterialTheme.colorScheme.outlineVariant
                                    }
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(18.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(16.dp),
                                            color = if (conseguido) {
                                                MaterialTheme.colorScheme.secondaryContainer
                                            } else {
                                                MaterialTheme.colorScheme.primaryContainer
                                            },
                                            modifier = Modifier.size(52.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = if (conseguido) {
                                                        Icons.Filled.EmojiEvents
                                                    } else {
                                                        Icons.Filled.Flag
                                                    },
                                                    contentDescription = null,
                                                    tint = if (conseguido) {
                                                        MaterialTheme.colorScheme.onSecondaryContainer
                                                    } else {
                                                        MaterialTheme.colorScheme.onPrimaryContainer
                                                    },
                                                    modifier = Modifier.size(28.dp)
                                                )
                                            }
                                        }
                                        Column(
                                            modifier = Modifier.weight(1f),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                reconocimiento.titulo,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Surface(
                                                shape = RoundedCornerShape(50),
                                                color = if (conseguido) {
                                                    MaterialTheme.colorScheme.secondaryContainer
                                                } else {
                                                    MaterialTheme.colorScheme.primaryContainer
                                                }
                                            ) {
                                                Text(
                                                    if (conseguido) "CONSEGUIDO" else "EN PROGRESO",
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (conseguido) {
                                                        MaterialTheme.colorScheme.onSecondaryContainer
                                                    } else {
                                                        MaterialTheme.colorScheme.onPrimaryContainer
                                                    }
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        reconocimiento.descripcion,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (meta != null) {
                                        val progreso = (meta - reconocimiento.faltantes).coerceIn(0, meta)
                                        LinearProgressIndicator(
                                            progress = { progreso.toFloat() / meta },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(7.dp)
                                                .clip(RoundedCornerShape(50)),
                                            color = colorLogro,
                                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                                        )
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                "$progreso / $meta ${reconocimiento.unidad}",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            if (conseguido) {
                                                Icon(
                                                    Icons.Filled.CheckCircle,
                                                    contentDescription = "Logro conseguido",
                                                    tint = colorLogro,
                                                    modifier = Modifier.size(19.dp)
                                                )
                                            }
                                        }
                                    }
                                    if (conseguido) {
                                        reconocimiento.fechaObtenida?.let { fecha ->
                                            Text(
                                                "Conseguido el ${fecha.format(formatoFecha)}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    } else {
                                        Text(
                                            mensajeFaltantes(reconocimiento),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            Text(
                                "Los reconocimientos se calculan a partir de pagos confirmados dentro del plazo. No modifican tus compromisos ni los montos pendientes.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
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
