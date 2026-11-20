package cl.fintech.pagoflex.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cl.fintech.pagoflex.data.remote.PagoRemoto
import cl.fintech.pagoflex.ui.components.BotonPrincipal
import cl.fintech.pagoflex.ui.components.indicadorDesplazamiento
import cl.fintech.pagoflex.util.formatearFecha
import java.text.NumberFormat
import java.time.LocalDate
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComprobanteScreen(
    pago: PagoRemoto,
    folio: String,
    concepto: String,
    nombreEmpresa: String,
    nombrePagador: String,
    rutParcial: String,
    onVolver: () -> Unit
) {
    val formatoMonto = NumberFormat.getNumberInstance(Locale.forLanguageTag("es-CL")).apply {
        minimumFractionDigits = 0
        maximumFractionDigits = 2
    }
    val numeroComprobante = pago.referenciaComprobante
        ?.takeIf { it.isNotBlank() }
        ?: "Pago PagoFlex #${pago.idPago}"
    val fecha = runCatching { formatearFecha(LocalDate.parse(pago.fechaPago)) }
        .getOrDefault(pago.fechaPago)
    val confirmado = pago.estadoSincronizacion.equals("CONFIRMADO", ignoreCase = true)
    val modoOscuro = isSystemInDarkTheme()
    val coloresCabecera = if (modoOscuro) {
        listOf(Color(0xFF263A91), Color(0xFF543184))
    } else {
        listOf(Color(0xFF3448D4), Color(0xFF7545BA))
    }
    val colorEstado = if (confirmado) Color(0xFF0C6B53) else Color(0xFF8B5518)
    val fondoEstado = if (confirmado) Color(0xFFD8F8E9) else Color(0xFFFFEDCA)
    val estadoDesplazamiento = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Comprobante de pago") },
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
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .indicadorDesplazamiento(
                    estado = estadoDesplazamiento,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.65f)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(estadoDesplazamiento)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(26.dp))
                        .background(Brush.linearGradient(coloresCabecera))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 26.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "PAGOFLEX · COMPROBANTE DIGITAL",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE4E8FF),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            "$${formatoMonto.format(pago.monto)}",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            "Monto registrado en CLP",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFE4E8FF)
                        )
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = fondoEstado
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                                horizontalArrangement = Arrangement.spacedBy(7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (confirmado) {
                                    Icon(
                                        imageVector = Icons.Filled.CheckCircle,
                                        contentDescription = null,
                                        tint = colorEstado
                                    )
                                }
                                Text(
                                    if (confirmado) "Pago confirmado" else "Pendiente de confirmación",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = colorEstado
                                )
                            }
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            "Datos de la operación",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        DatoRecibo("Fecha de pago", fecha)
                        DatoRecibo("Empresa", nombreEmpresa)
                        DatoRecibo("Concepto", concepto)
                        DatoRecibo("Medio de pago", pago.metodoPago)
                        DatoRecibo("Canal", "PagoFlex")

                        Divider(color = MaterialTheme.colorScheme.outlineVariant)

                        Text(
                            "Identificación",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        DatoRecibo("N.° de comprobante", numeroComprobante)
                        DatoRecibo("Folio del compromiso", folio)

                        Divider(color = MaterialTheme.colorScheme.outlineVariant)

                        Text(
                            "Titular",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        DatoRecibo("Pagado por", nombrePagador)
                        DatoRecibo("RUT", rutParcial)
                    }
                }

                Text(
                    "Este comprobante muestra la información del pago registrada en PagoFlex.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                BotonPrincipal(
                    texto = "Volver",
                    onClick = onVolver,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun DatoRecibo(etiqueta: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.42f)
        )
        Text(
            text = valor.ifBlank { "No especificado" },
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(0.58f)
        )
    }
}
