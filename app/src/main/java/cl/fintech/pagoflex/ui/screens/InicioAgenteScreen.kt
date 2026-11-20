package cl.fintech.pagoflex.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cl.fintech.pagoflex.model.Reporte
import cl.fintech.pagoflex.model.Usuario
import cl.fintech.pagoflex.ui.components.indicadorDesplazamiento
import java.time.format.DateTimeFormatter

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
    val estadoLista = rememberLazyListState()

    val abiertos = reportes.count { it.estado == "Abierto" }
    val enGestion = reportes.count { it.estado == "En gestión" }
    val resueltos = reportes.count { it.estado == "Resuelto" }
    val porResolver = abiertos + enGestion

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

    val modoOscuro = isSystemInDarkTheme()
    val coloresCabecera = if (modoOscuro) {
        listOf(Color(0xFF263A91), Color(0xFF543184))
    } else {
        listOf(Color(0xFF3448D4), Color(0xFF7545BA))
    }
    val formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy · HH:mm")
    val entradaCabecera = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        entradaCabecera.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 420, easing = FastOutSlowInEasing)
        )
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
                    ) {
                        Text("Salir")
                    }
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
                state = estadoLista,
                modifier = Modifier
                    .fillMaxSize()
                    .indicadorDesplazamiento(
                        estado = estadoLista,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.65f)
                    ),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(26.dp))
                            .background(Brush.linearGradient(coloresCabecera))
                            .graphicsLayer {
                                alpha = entradaCabecera.value
                                translationY = (1f - entradaCabecera.value) * 18.dp.toPx()
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = "CENTRO DE ATENCIÓN",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE4E8FF)
                            )
                            Text(
                                text = "Gestiona y resuelve reportes",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "${usuario.nombreCompleto} · ${usuario.rol.etiqueta}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFFDEE5FF),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                MetricaAgente(
                                    cantidad = porResolver,
                                    etiqueta = "Por resolver",
                                    modifier = Modifier.weight(1f)
                                )
                                MetricaAgente(
                                    cantidad = enGestion,
                                    etiqueta = "En gestión",
                                    modifier = Modifier.weight(1f)
                                )
                                MetricaAgente(
                                    cantidad = resueltos,
                                    etiqueta = "Resueltos",
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                if (errorReporte != null) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    errorReporte,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                                TextButton(onClick = onActualizar) {
                                    Text("Reintentar")
                                }
                            }
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Explorar reportes",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Filtra por estado o busca por número, compromiso y motivo.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(
                                    "Por resolver", "Abiertos", "En gestión", "Resueltos", "Todos"
                                ).forEach { filtro ->
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
                            AnimatedVisibility(
                                visible = filtroEstado != "Por resolver" || textoBusqueda.isNotBlank(),
                                enter = fadeIn(animationSpec = tween(180)) +
                                        expandVertically(animationSpec = tween(220)),
                                exit = fadeOut(animationSpec = tween(150)) +
                                        shrinkVertically(animationSpec = tween(220))
                            ) {
                                TextButton(
                                    onClick = {
                                        filtroEstado = "Por resolver"
                                        textoBusqueda = ""
                                    }
                                ) {
                                    Text("Limpiar filtros")
                                }
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Bandeja de reportes",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Crossfade(
                            targetState = reportesVisibles.size,
                            animationSpec = tween(durationMillis = 240),
                            label = "cantidadReportesVisibles"
                        ) { cantidad ->
                            Text(
                                text = "$cantidad resultados",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                if (reportesVisibles.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(22.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                if (cargandoReportes) {
                                    CircularProgressIndicator()
                                }
                                Text(
                                    text = when {
                                        cargandoReportes -> "Cargando reportes..."
                                        reportes.isEmpty() -> "No hay reportes para mostrar."
                                        busqueda.isNotBlank() -> "No se encontraron reportes con esa búsqueda."
                                        else -> "No hay reportes para el filtro seleccionado."
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(reportesVisibles, key = { it.idReporte }) { reporte ->
                        val colorEstado = when (reporte.estado) {
                            "Abierto" -> if (modoOscuro) Color(0xFFFFD47A) else Color(0xFF916000)
                            "En gestión" -> if (modoOscuro) Color(0xFFBEC6FF) else Color(0xFF4E56BC)
                            "Resuelto" -> if (modoOscuro) Color(0xFF78E6BA) else Color(0xFF087C5B)
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }

                        val colorAnimado by animateColorAsState(
                            targetValue = colorEstado,
                            animationSpec = tween(durationMillis = 280),
                            label = "colorEstadoReporte"
                        )
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .size(width = 400.dp, height = 5.dp)
                                    .background(colorAnimado)
                            )
                            Column(
                                modifier = Modifier.padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        modifier = Modifier.size(44.dp),
                                        shape = RoundedCornerShape(14.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                "#",
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        }
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Reporte #${reporte.idReporte}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Compromiso: ${reporte.folio}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = colorAnimado.copy(alpha = if (modoOscuro) 0.18f else 0.11f)
                                ) {
                                    Text(
                                        text = reporte.estado,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = colorAnimado
                                    )
                                }
                                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                    Text(
                                        text = reporte.motivo,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = reporte.comentario?.takeIf { it.isNotBlank() }
                                            ?: "Sin comentario adicional",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Creado: ${reporte.fechaCreacion.format(formatoFecha)}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                if (reporte.estado == "Abierto") {
                                    Button(
                                        onClick = { onTomarReporte(reporte.idReporte) },
                                        modifier = Modifier.fillMaxWidth(),
                                        enabled = !operandoReporte
                                    ) {
                                        Text("Tomar reporte")
                                    }
                                }

                                if (reporte.estado == "En gestión" && reporte.idAgente == usuario.id) {
                                    var respuesta by rememberSaveable { mutableStateOf("") }
                                    var mostrarError by rememberSaveable { mutableStateOf(false) }

                                    Text(
                                        text = "RESPONDER AL USUARIO",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
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
                                        modifier = Modifier.fillMaxWidth(),
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
                                    Surface(
                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(14.dp),
                                            verticalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                "Respuesta enviada",
                                                style = MaterialTheme.typography.labelLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer
                                            )
                                            Text(
                                                reporte.respuesta,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricaAgente(
    cantidad: Int,
    etiqueta: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.13f))
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Crossfade(
            targetState = cantidad,
            animationSpec = tween(durationMillis = 280),
            label = "cantidadMetricaAgente"
        ) { valor ->
            Text(
                text = valor.toString(),
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = etiqueta,
            color = Color(0xFFE4E8FF),
            style = MaterialTheme.typography.labelSmall,
            maxLines = 2
        )
    }
}
