package cl.fintech.pagoflex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import cl.fintech.pagoflex.model.EstadoCompromiso
import cl.fintech.pagoflex.model.Usuario
import cl.fintech.pagoflex.ui.components.TarjetaCompromiso
import cl.fintech.pagoflex.ui.components.indicadorDesplazamiento
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import cl.fintech.pagoflex.viewmodel.EstadoCotizaciones
import java.util.Locale
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.FilterChip
import androidx.compose.runtime.saveable.rememberSaveable
import cl.fintech.pagoflex.util.ResumenCompromisos
import cl.fintech.pagoflex.util.formatearFecha
import cl.fintech.pagoflex.util.formatearPesos
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompromisosScreen(
    usuario: Usuario,
    compromisos: List<Compromiso>,
    estadoCotizaciones: EstadoCotizaciones,
    nombreEmpresa: (String) -> String,
    importeDe: (Compromiso) -> Int,
    onVerDetalle: (String) -> Unit,
    onVerHistorial: () -> Unit,
    onVerReconocimientos: () -> Unit,
    onVerPerfil: () -> Unit,
    onSalir: () -> Unit,
    resumenCompromisos: ResumenCompromisos? = null,
    cargandoCompromisos: Boolean = false,
    errorCompromisos: String? = null,
    onActualizar: () -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val estadoLista = rememberLazyListState()

    var mostrarDialogo by remember { mutableStateOf(false) }

    BackHandler(enabled = drawerState.isClosed && !mostrarDialogo) {
        mostrarDialogo = true
    }

    var monedaSeleccionada by rememberSaveable { mutableStateOf("CLP") }
    var filtroEstado by rememberSaveable { mutableStateOf("Por pagar") }

    val compromisosVisibles = when (filtroEstado) {
        "Por pagar" -> compromisos.filter { it.estado != EstadoCompromiso.PAGADO }
        "Pendientes" -> compromisos.filter { it.estado == EstadoCompromiso.PENDIENTE }
        "Vencidos" -> compromisos.filter { it.estado == EstadoCompromiso.VENCIDO }
        "Pagados" -> compromisos.filter { it.estado == EstadoCompromiso.PAGADO }
        else -> compromisos
    }

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
            PullToRefreshBox(
                isRefreshing = cargandoCompromisos,
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
                                        "EUR" -> (estadoCotizaciones.valorEuro ?: 0.0) > 0.0
                                        "USD" -> (estadoCotizaciones.valorDolar ?: 0.0) > 0.0
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


                            when {
                                estadoCotizaciones.cargando -> {
                                    Text(
                                        text = "Consultando cotizaciones de EUR y USD...",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }

                                estadoCotizaciones.error != null -> {
                                    Text(
                                        text = estadoCotizaciones.error,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }

                                estadoCotizaciones.valorEuro == null || estadoCotizaciones.valorDolar == null -> {
                                    Text(
                                        text = "Las cotizaciones aún no están disponibles.",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }


                            if (monedaSeleccionada != "CLP") {
                                val fecha = when (monedaSeleccionada) {
                                    "EUR" -> estadoCotizaciones.fechaEuro
                                    "USD" -> estadoCotizaciones.fechaDolar
                                    else -> null
                                }

                                val fechaFormateada = fecha
                                    ?.split("-")
                                    ?.reversed()
                                    ?.joinToString("-") ?: "No disponible"


                                val cotizacionActual = when (monedaSeleccionada) {
                                    "EUR" -> estadoCotizaciones.valorEuro
                                    "USD" -> estadoCotizaciones.valorDolar
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
                                            "Fuente: Currency API (fawazahmed0)"
                                    ,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }


                    if (resumenCompromisos != null && !cargandoCompromisos && errorCompromisos == null) {
                        item {
                            val esOscuro = isSystemInDarkTheme()
                            val proximoVencimiento = resumenCompromisos.proximoVencimiento?.let {
                                formatearFecha(it)
                            } ?: "Sin próximos vencimientos"
                            val cantidadVencidos = resumenCompromisos.cantidadVencidos
                            val coloresFondo = if (esOscuro) {
                                listOf(Color(0xFF273A91), Color(0xFF4058AC))
                            } else {
                                listOf(Color(0xFF293FC2), Color(0xFF536BE1))
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(26.dp))
                                    .background(Brush.linearGradient(coloresFondo))
                                    .padding(20.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "TU PANORAMA FINANCIERO",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White.copy(alpha = 0.85f)
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(50),
                                            color = Color.White.copy(alpha = 0.17f)
                                        ) {
                                            Text(
                                                text = "ESTE MES",
                                                modifier = Modifier.padding(
                                                    horizontal = 10.dp,
                                                    vertical = 5.dp
                                                ),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color.White
                                            )
                                        }
                                    }

                                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                        Text(
                                            text = "Por pagar este mes",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.White.copy(alpha = 0.9f)
                                        )
                                        Text(
                                            text = formatearPesos(resumenCompromisos.totalPorPagarMes),
                                            style = MaterialTheme.typography.headlineLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "Pesos chilenos · CLP",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.White.copy(alpha = 0.76f)
                                        )
                                    }

                                    HorizontalDivider(color = Color.White.copy(alpha = 0.25f))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.Top,
                                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.weight(1f),
                                            verticalArrangement = Arrangement.spacedBy(5.dp)
                                        ) {
                                            Text(
                                                text = "PRÓXIMO VENCIMIENTO",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color.White.copy(alpha = 0.8f)
                                            )
                                            Text(
                                                text = proximoVencimiento,
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.White
                                            )
                                        }

                                        Column(
                                            horizontalAlignment = Alignment.End,
                                            verticalArrangement = Arrangement.spacedBy(5.dp)
                                        ) {
                                            Text(
                                                text = "VENCIDOS",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color.White.copy(alpha = 0.8f)
                                            )
                                            Surface(
                                                shape = RoundedCornerShape(50),
                                                color = Color.White.copy(alpha = 0.17f)
                                            ) {
                                                Text(
                                                    text = "$cantidadVencidos",
                                                    modifier = Modifier.padding(
                                                        horizontal = 14.dp,
                                                        vertical = 5.dp
                                                    ),
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (cantidadVencidos > 0) {
                                                        Color(0xFFFFD6A1)
                                                    } else {
                                                        Color(0xFFA5F4D1)
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "Filtrar compromisos",
                                style = MaterialTheme.typography.titleMedium
                            )

                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("Por pagar", "Pendientes", "Vencidos", "Pagados", "Todos").forEach { filtro ->
                                    FilterChip(
                                        selected = filtroEstado == filtro,
                                        onClick = { filtroEstado = filtro },
                                        label = { Text(filtro) }
                                    )
                                }
                            }
                        }
                    }

                    when {
                        cargandoCompromisos -> {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }

                        errorCompromisos != null -> {
                            item {
                                Card(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = errorCompromisos,
                                        color = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            }
                        }

                        compromisosVisibles.isEmpty() -> {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        when {
                                            compromisos.isEmpty() -> "No tienes compromisos registrados"
                                            filtroEstado == "Pendientes" -> "No tienes compromisos pendientes para mostrar."
                                            filtroEstado == "Vencidos" -> "No tienes compromisos vencidos para mostrar."
                                            filtroEstado == "Pagados" -> "No tienes compromisos pagados para mostrar."
                                            filtroEstado == "Por pagar" -> "No tienes compromisos por pagar. Selecciona Pagados o Todos para ver los anteriores."
                                            else -> "No hay compromisos para mostrar."
                                        }
                                    )
                                }
                            }
                        }

                        else -> {
                            items(compromisosVisibles) { compromiso ->
                                val importe = importeDe(compromiso)

                                val cotizacion = when (monedaSeleccionada) {
                                    "EUR" -> estadoCotizaciones.valorEuro
                                    "USD" -> estadoCotizaciones.valorDolar
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
    }
}
