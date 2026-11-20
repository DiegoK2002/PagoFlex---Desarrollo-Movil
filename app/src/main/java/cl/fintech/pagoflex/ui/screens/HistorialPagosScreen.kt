package cl.fintech.pagoflex.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cl.fintech.pagoflex.data.remote.PagoRemoto
import cl.fintech.pagoflex.model.Compromiso
import cl.fintech.pagoflex.model.EstadoCompromiso
import cl.fintech.pagoflex.ui.components.GraficoCumplimiento
import cl.fintech.pagoflex.ui.components.ListaDesplegable
import cl.fintech.pagoflex.ui.components.BotonSecundario
import cl.fintech.pagoflex.ui.components.indicadorDesplazamiento
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
    var estadoSeleccionado by rememberSaveable { mutableStateOf("Todos los estados") }
    var montoMinimo by rememberSaveable { mutableStateOf("") }
    var montoMaximo by rememberSaveable { mutableStateOf("") }
    val estadoLista = rememberLazyListState()

    val entrada = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        entrada.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 420, easing = FastOutSlowInEasing)
        )
    }

    val compromisosPorId = compromisos.mapNotNull { compromiso ->
        compromiso.folio.removePrefix("REM-").toLongOrNull()?.let { id ->
            id to compromiso
        }
    }.toMap()

    val opcionesEstados = listOf(
        "Todos los estados",
        "Confirmados",
        "Pendientes de confirmación"
    )

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

    val montoMinimoNumero = montoMinimo.toBigDecimalOrNull()
    val montoMaximoNumero = montoMaximo.toBigDecimalOrNull()

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
        val coincideEstado = when (estadoSeleccionado) {
            "Confirmados" -> pago.estadoSincronizacion.equals("CONFIRMADO", ignoreCase = true)
            "Pendientes de confirmación" -> pago.estadoSincronizacion.equals("PENDIENTE", ignoreCase = true)
            else -> true
        }
        val coincideMonto =
            (montoMinimoNumero == null || pago.monto >= montoMinimoNumero) &&
                    (montoMaximoNumero == null || pago.monto <= montoMaximoNumero)
        coincideEmpresa && coincidePeriodo && coincideEstado && coincideMonto
    }

    val inicioEvaluacion = hoy.minusMonths(12)

    val pagosConfirmadosPorCompromiso = pagos.asSequence()
        .filter { pago ->
            pago.activo &&
                    pago.estadoSincronizacion.equals("CONFIRMADO", ignoreCase = true)
        }
        .mapNotNull { pago ->
            val fechaPago = runCatching { LocalDate.parse(pago.fechaPago) }.getOrNull()
                ?: return@mapNotNull null
            (pago.idUsuario to pago.idCompromiso) to fechaPago
        }
        .groupBy(
            keySelector = { it.first },
            valueTransform = { it.second }
        )
        .mapValues { (_, fechas) -> fechas.minOrNull() }

    val compromisosEvaluables = compromisos.filter { compromiso ->
        val idCompromiso = compromiso.folio.removePrefix("REM-").toLongOrNull()
        val fechaPago = idCompromiso?.let {
            pagosConfirmadosPorCompromiso[compromiso.idUsuario to it]
        }
        val vencimientoEnPeriodo =
            compromiso.fechaVencimiento.isBefore(hoy) &&
                    !compromiso.fechaVencimiento.isBefore(inicioEvaluacion)
        val pagoEnPeriodo = fechaPago != null &&
                !fechaPago.isBefore(inicioEvaluacion) &&
                !fechaPago.isAfter(hoy)

        compromiso.estado != EstadoCompromiso.ANULADO &&
                compromiso.estado != EstadoCompromiso.EN_REVISION &&
                (vencimientoEnPeriodo || pagoEnPeriodo)
    }

    val pagosATiempo = compromisosEvaluables.count { compromiso ->
        val idCompromiso = compromiso.folio.removePrefix("REM-").toLongOrNull()
        val fechaPago = idCompromiso?.let {
            pagosConfirmadosPorCompromiso[compromiso.idUsuario to it]
        }
        fechaPago != null && !fechaPago.isAfter(compromiso.fechaVencimiento)
    }
    val porcentajeCumplimiento = if (compromisosEvaluables.isEmpty()) {
        null
    } else {
        pagosATiempo * 100.0 / compromisosEvaluables.size
    }
    val categoriaCumplimiento = when {
        porcentajeCumplimiento == null -> null
        compromisosEvaluables.size < 3 -> "Resultado provisional"
        porcentajeCumplimiento >= 95.0 -> "Ejemplar"
        porcentajeCumplimiento >= 80.0 -> "Al día"
        else -> "En camino"
    }

    val numeroFormato = NumberFormat.getNumberInstance(Locale.forLanguageTag("es-CL")).apply {
        minimumFractionDigits = 0
        maximumFractionDigits = 2
    }
    val fechaFormato = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val modoOscuro = isSystemInDarkTheme()
    val coloresCabecera = if (modoOscuro) {
        listOf(Color(0xFF263A91), Color(0xFF543184))
    } else {
        listOf(Color(0xFF3448D4), Color(0xFF7545BA))
    }
    val totalConfirmados = pagos.count {
        it.activo && it.estadoSincronizacion.equals("CONFIRMADO", ignoreCase = true)
    }
    val filtroActivo = empresaSeleccionada.isNotEmpty() ||
            periodoSeleccionado != "Todo el historial" ||
            estadoSeleccionado != "Todos los estados" ||
            montoMinimo.isNotEmpty() || montoMaximo.isNotEmpty()

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
        PullToRefreshBox(
            isRefreshing = cargando,
            onRefresh = onActualizar,
            modifier = Modifier.fillMaxSize().padding(innerPadding)
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer {
                                alpha = entrada.value
                                translationY = (1f - entrada.value) * 12.dp.toPx()
                            }
                            .clip(RoundedCornerShape(26.dp))
                            .background(Brush.linearGradient(coloresCabecera))
                    ) {
                        Column(
                            modifier = Modifier.padding(22.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = "TU HISTORIAL",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE4E8FF)
                            )
                            Crossfade(
                                targetState = pagos.size,
                                animationSpec = tween(durationMillis = 300),
                                label = "Cantidad de pagos"
                            ) { cantidad ->
                                Text(
                                    text = "$cantidad",
                                    style = MaterialTheme.typography.displaySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Text(
                                text = "pagos registrados",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFFE4E8FF)
                            )
                            Surface(
                                color = Color.White.copy(alpha = 0.16f),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Crossfade(
                                    targetState = totalConfirmados,
                                    animationSpec = tween(durationMillis = 300),
                                    label = "Pagos confirmados"
                                ) { cantidad ->
                                    Text(
                                        text = "$cantidad pagos confirmados",
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                                        style = MaterialTheme.typography.labelLarge,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer {
                                alpha = entrada.value
                                translationY = (1f - entrada.value) * 8.dp.toPx()
                            },
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                "Cumplimiento de los últimos 12 meses",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            if (porcentajeCumplimiento == null) {
                                Text(
                                    "Todavía no hay compromisos evaluables en este período.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                GraficoCumplimiento(
                                    porcentaje = porcentajeCumplimiento,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        categoriaCumplimiento.orEmpty(),
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                                Text(
                                    "$pagosATiempo de ${compromisosEvaluables.size} compromisos pagados a tiempo",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (compromisosEvaluables.size < 3) {
                                    Text(
                                        "Porcentaje provisional: se necesitan al menos 3 compromisos evaluables para una clasificación definitiva.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
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
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Text(
                                    "Explorar movimientos",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Encuentra tus pagos por empresa, período, estado o importe.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
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
                                ListaDesplegable(
                                    etiqueta = "Filtrar por período",
                                    opciones = opcionesPeriodos,
                                    seleccionada = periodoSeleccionado,
                                    textoDe = { it },
                                    onSeleccionar = { periodoSeleccionado = it },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                ListaDesplegable(
                                    etiqueta = "Filtrar por estado",
                                    opciones = opcionesEstados,
                                    seleccionada = estadoSeleccionado,
                                    textoDe = { it },
                                    onSeleccionar = { estadoSeleccionado = it },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Text(
                                    "Monto pagado (CLP)",
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = montoMinimo,
                                        onValueChange = { montoMinimo = it.filter(Char::isDigit).take(12) },
                                        label = { Text("Mínimo") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                    OutlinedTextField(
                                        value = montoMaximo,
                                        onValueChange = { montoMaximo = it.filter(Char::isDigit).take(12) },
                                        label = { Text("Máximo") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                AnimatedVisibility(
                                    visible = filtroActivo,
                                    enter = fadeIn(animationSpec = tween(220)) +
                                            expandVertically(animationSpec = tween(220)),
                                    exit = fadeOut(animationSpec = tween(150)) +
                                            shrinkVertically(animationSpec = tween(180))
                                ) {
                                    BotonSecundario(
                                        texto = "Limpiar filtros",
                                        onClick = {
                                            empresaSeleccionada = ""
                                            periodoSeleccionado = "Todo el historial"
                                            estadoSeleccionado = "Todos los estados"
                                            montoMinimo = ""
                                            montoMaximo = ""
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Movimientos",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Crossfade(
                                targetState = pagosFiltrados.size,
                                animationSpec = tween(durationMillis = 250),
                                label = "Resultados filtrados"
                            ) { cantidad ->
                                Text(
                                    "$cantidad resultados",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
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
                        val confirmado = pago.estadoSincronizacion.equals("CONFIRMADO", ignoreCase = true)
                        val pendiente = pago.estadoSincronizacion.equals("PENDIENTE", ignoreCase = true)
                        val estado = when {
                            confirmado -> "Confirmado"
                            pendiente -> "Pendiente de confirmación"
                            else -> "Estado de confirmación no disponible"
                        }
                        val colorEstado = when {
                            confirmado -> if (modoOscuro) Color(0xFF78E6BA) else Color(0xFF087C5B)
                            pendiente -> if (modoOscuro) Color(0xFFFFD47A) else Color(0xFF916000)
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                        val empresa = compromiso?.let { nombreEmpresa(it.codigoEmpresa) }
                            ?: "Empresa no disponible"

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
                                    .height(5.dp)
                                    .background(colorEstado)
                            )
                            Column(
                                modifier = Modifier.padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Surface(
                                        modifier = Modifier.size(42.dp),
                                        shape = RoundedCornerShape(14.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                empresa.trim().take(1).uppercase().ifEmpty { "P" },
                                                style = MaterialTheme.typography.titleLarge,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        }
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            empresa,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            compromiso?.concepto ?: "Compromiso #${pago.idCompromiso}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = colorEstado.copy(alpha = if (modoOscuro) 0.18f else 0.11f)
                                ) {
                                    Text(
                                        estado,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = colorEstado
                                    )
                                }
                                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                    Text(
                                        "$${numeroFormato.format(pago.monto)}",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "Monto pagado · CLP",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(18.dp)
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            "FECHA DE PAGO",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            fecha?.format(fechaFormato) ?: pago.fechaPago,
                                            style = MaterialTheme.typography.labelLarge
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            "MÉTODO",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            pago.metodoPago,
                                            style = MaterialTheme.typography.labelLarge,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                                Text(
                                    "Pago #${pago.idPago}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                pago.referenciaComprobante?.takeIf { it.isNotBlank() }?.let {
                                    Text(
                                        "Referencia: $it",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
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
}
