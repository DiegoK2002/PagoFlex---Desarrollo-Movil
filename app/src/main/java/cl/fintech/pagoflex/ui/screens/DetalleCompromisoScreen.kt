package cl.fintech.pagoflex.ui.screens

import android.content.ActivityNotFoundException
import android.content.Intent
import android.provider.CalendarContract
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.fintech.pagoflex.model.Compromiso
import cl.fintech.pagoflex.model.EstadoCompromiso
import cl.fintech.pagoflex.model.MotivoNoPago
import cl.fintech.pagoflex.model.Reporte
import cl.fintech.pagoflex.ui.components.BotonPrincipal
import cl.fintech.pagoflex.ui.components.BotonSecundario
import cl.fintech.pagoflex.ui.components.colorDeEstado
import cl.fintech.pagoflex.ui.components.textoConcepto
import cl.fintech.pagoflex.ui.theme.Dimens
import cl.fintech.pagoflex.util.formatearFecha
import cl.fintech.pagoflex.util.formatearPesos
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleCompromisoScreen(
    compromiso: Compromiso,
    nombreEmpresa: String,
    diasAtraso: Int,
    recargo: Int,
    total: Int,
    motivoNoPago: MotivoNoPago?,
    anterior: Compromiso?,
    onVolver: () -> Unit,
    onConfirmarPago: () -> Unit,
    onVerComprobante: () -> Unit,
    onCrearReporte: (String, String, (Boolean) -> Unit) -> Unit,
    reporteActual: Reporte?,
    operacionesHabilitadas: Boolean = false,
    procesandoReporte: Boolean = false,
    pagosHabilitados: Boolean = false,
    registrandoPago: Boolean = false,
    errorPago: String? = null
) {
    var mostrarConfirmacion by remember { mutableStateOf(false) }
    var mostrarFormulario by remember { mutableStateOf(false) }
    var motivoReporte by remember { mutableStateOf("Ya pagué por otro medio") }
    var comentarioReporte by remember { mutableStateOf("") }
    var errorReporte by remember { mutableStateOf(false) }
    var mostrarReferencia by rememberSaveable { mutableStateOf(false) }

    val estaPagado = compromiso.estado == EstadoCompromiso.PAGADO
    val admiteGestiones = compromiso.estado == EstadoCompromiso.PENDIENTE ||
            compromiso.estado == EstadoCompromiso.VENCIDO
    val puedeReportar = operacionesHabilitadas && admiteGestiones
    val puedeAgendar = admiteGestiones && !compromiso.fechaVencimiento.isBefore(LocalDate.now())
    val contexto = LocalContext.current
    val colorEstado = colorDeEstado(compromiso.estado)
    val diasHastaVencimiento = ChronoUnit.DAYS.between(
        LocalDate.now(), compromiso.fechaVencimiento
    )

    val mensajeOrientacion = when {
        estaPagado -> "Este compromiso ya está pagado. Puedes consultar su comprobante."
        compromiso.estado == EstadoCompromiso.ANULADO ->
            "Este compromiso fue anulado. No necesitas registrar ningún pago."
        compromiso.estado == EstadoCompromiso.EN_REVISION ->
            "El compromiso está en revisión. Consulta el seguimiento de tu reporte."
        motivoNoPago != null -> textoMotivo(motivoNoPago, anterior)
        diasHastaVencimiento < 0 -> {
            val dias = -diasHastaVencimiento
            if (dias == 1L) "El plazo venció hace 1 día. Revisa el recargo antes de registrar el pago."
            else "El plazo venció hace $dias días. Revisa el recargo antes de registrar el pago."
        }
        diasHastaVencimiento == 0L -> "Vence hoy. Puedes registrar el pago ahora."
        diasHastaVencimiento == 1L -> "Vence mañana. Puedes pagarlo hoy o agendar un recordatorio."
        else -> "Faltan $diasHastaVencimiento días para el vencimiento. Puedes pagar por adelantado o agendar un recordatorio."
    }

    val agregarAlCalendario: () -> Unit = {
        val inicio = compromiso.fechaVencimiento
            .atStartOfDay(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()
        val fin = compromiso.fechaVencimiento
            .plusDays(1)
            .atStartOfDay(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()

        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, inicio)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, fin)
            putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true)
            putExtra(CalendarContract.Events.TITLE, "PagoFlex - ${textoConcepto(compromiso)}")
            putExtra(
                CalendarContract.Events.DESCRIPTION,
                "Vencimiento con $nombreEmpresa. " +
                        "Folio: ${compromiso.folio}. " +
                        "Consulta PagoFlex para revisar tu compromiso."
            )
        }
        try {
            contexto.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                contexto,
                "No hay una aplicación de calendario disponible.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestionar compromiso") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(Dimens.espacioPantalla),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(
                    text = nombreEmpresa,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = textoConcepto(compromiso),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, colorEstado.copy(alpha = 0.45f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .background(colorEstado, RoundedCornerShape(50))
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            text = "Situación actual · ${compromiso.estado.etiqueta}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = mensajeOrientacion,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
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
                        text = when {
                            estaPagado -> "PAGO REGISTRADO"
                            admiteGestiones -> "REGISTRAR PAGO"
                            else -> "ESTADO DEL COMPROMISO"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    if (estaPagado || admiteGestiones) {
                        Text(
                            text = formatearPesos(total),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (estaPagado) "Importe registrado" else "Total a registrar en CLP",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (recargo > 0 && (estaPagado || admiteGestiones)) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Monto original", style = MaterialTheme.typography.bodySmall)
                                    Text(formatearPesos(compromiso.monto), style = MaterialTheme.typography.bodySmall)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Recargo por atraso", style = MaterialTheme.typography.bodySmall)
                                    Text(formatearPesos(recargo), style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }

                    if (estaPagado) {
                        if (operacionesHabilitadas || pagosHabilitados) {
                            BotonPrincipal(
                                texto = "Consultar comprobante",
                                onClick = onVerComprobante,
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            Text(
                                "El comprobante descargable aún no está disponible.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else if (admiteGestiones && pagosHabilitados) {
                        BotonPrincipal(
                            texto = "Registrar pago",
                            onClick = { mostrarConfirmacion = true },
                            habilitado = motivoNoPago == null && !registrandoPago,
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (registrandoPago) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(22.dp))
                                Text("Registrando el pago en el servidor...")
                            }
                        }
                        if (errorPago != null) {
                            Text(errorPago, color = MaterialTheme.colorScheme.error)
                        }
                    } else if (admiteGestiones) {
                        Text(
                            "El registro de pagos no está disponible en este momento.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            "No hay acciones de pago disponibles para este estado.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (puedeAgendar || puedeReportar) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "También puedes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (puedeAgendar) {
                        BotonSecundario(
                            texto = "Agendar vencimiento en calendario",
                            onClick = agregarAlCalendario,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    if (puedeReportar) {
                        BotonSecundario(
                            texto = "Reportar un problema con este cobro",
                            onClick = {
                                motivoReporte = "Ya pagué por otro medio"
                                comentarioReporte = ""
                                errorReporte = false
                                mostrarFormulario = true
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            if (operacionesHabilitadas && reporteActual != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Seguimiento de tu reporte",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text("Estado: ${reporteActual.estado}")
                        Text("Motivo: ${reporteActual.motivo}")
                        if (reporteActual.respuesta.isNullOrBlank()) {
                            Text("Todavía no hay respuesta del agente.")
                        } else {
                            Text("Respuesta del agente: ${reporteActual.respuesta}")
                        }
                    }
                }
            }

            TextButton(
                onClick = { mostrarReferencia = !mostrarReferencia },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (mostrarReferencia) "Ocultar datos de referencia  ▲"
                    else "Consultar datos de referencia  ▼"
                )
            }

            if (mostrarReferencia) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Folio: ${compromiso.folio}")
                        Text("Vencimiento: ${formatearFecha(compromiso.fechaVencimiento)}")
                        if (compromiso.numeroCuota != null && compromiso.totalCuotas != null) {
                            Text("Cuota ${compromiso.numeroCuota} de ${compromiso.totalCuotas}")
                        }
                        if (recargo == 0 && (estaPagado || admiteGestiones)) {
                            Text("Importe original: ${formatearPesos(compromiso.monto)}")
                        }
                    }
                }
            }
        }
    }

    if (pagosHabilitados && mostrarConfirmacion) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmacion = false },
            title = { Text("Registrar pago") },
            text = {
                Text(
                    "Se registrará un pago de ${formatearPesos(compromiso.monto)} " +
                            "para $nombreEmpresa, con método TRANSFERENCIA. "
                )
            },
            confirmButton = {
                TextButton(
                    enabled = !registrandoPago,
                    onClick = {
                        mostrarConfirmacion = false
                        onConfirmarPago()
                    }
                ) { Text("Registrar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarConfirmacion = false }) { Text("Cancelar") }
            }
        )
    }


    if (operacionesHabilitadas && mostrarFormulario) {
        val motivos = listOf(
            "Ya pagué por otro medio",
            "No reconozco este cobro",
            "El monto no corresponde",
            "Otro"
        )

        AlertDialog(
            onDismissRequest = { mostrarFormulario = false },
            title = { Text("Reportar un problema") },
            text = {
                Column(
                    modifier = Modifier
                        .heightIn(max = 380.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    motivos.forEach { opcion ->
                        FilterChip(
                            selected = motivoReporte == opcion,
                            onClick = {
                                motivoReporte = opcion
                                errorReporte = false
                            },
                            label = { Text(opcion) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    OutlinedTextField(
                        value = comentarioReporte,
                        onValueChange = {
                            comentarioReporte = it
                            errorReporte = false
                        },
                        label = {
                            Text(
                                if (motivoReporte == "Otro")
                                    "Explicación obligatoria"
                                else
                                    "Comentario (opcional)"
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )

                    if (errorReporte) {
                        Text(
                            "No se pudo registrar el reporte.",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    enabled = !procesandoReporte &&
                            (motivoReporte != "Otro" || comentarioReporte.isNotBlank()),
                    onClick = {
                        onCrearReporte(motivoReporte, comentarioReporte) { guardado ->
                            if (guardado) {
                                mostrarFormulario = false
                                errorReporte = false
                            } else {
                                errorReporte = true
                            }
                        }
                    }
                ) {
                    Text("Enviar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarFormulario = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

}

private fun textoMotivo(motivo: MotivoNoPago, anterior: Compromiso?): String {
    val nombreAnterior = if (anterior != null) textoConcepto(anterior) else "el compromiso anterior"
    return when (motivo) {
        MotivoNoPago.YA_PAGADO -> "Este compromiso ya está pagado."
        MotivoNoPago.EN_REVISION -> "Este compromiso está en revisión por un reporte. Podrás pagarlo cuando se resuelva."
        MotivoNoPago.ANULADO -> "Este compromiso fue anulado, por lo que no tienes que pagarlo."
        MotivoNoPago.HAY_ANTERIOR -> "Primero debes pagar $nombreAnterior, que vence antes."
    }
}
