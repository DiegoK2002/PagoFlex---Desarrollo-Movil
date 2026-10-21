package cl.fintech.pagoflex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cl.fintech.pagoflex.model.Compromiso
import cl.fintech.pagoflex.model.EstadoCompromiso
import cl.fintech.pagoflex.model.MotivoNoPago
import cl.fintech.pagoflex.ui.components.BotonPrincipal
import cl.fintech.pagoflex.ui.components.BotonSecundario
import cl.fintech.pagoflex.ui.components.FilaDato
import cl.fintech.pagoflex.ui.components.colorDeEstado
import cl.fintech.pagoflex.ui.components.textoConcepto
import cl.fintech.pagoflex.ui.styles.EstilosTarjeta
import cl.fintech.pagoflex.ui.theme.Dimens
import cl.fintech.pagoflex.util.formatearFecha
import cl.fintech.pagoflex.util.formatearPesos
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.unit.dp
import cl.fintech.pagoflex.model.Reporte
import android.content.ActivityNotFoundException
import android.content.Intent
import android.provider.CalendarContract
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import java.time.LocalDate
import java.time.ZoneOffset

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
    onCrearReporte: (String, String) -> Boolean,
    reporteActual: Reporte?
) {

    var mostrarConfirmacion by remember { mutableStateOf(false) }

    var mostrarFormulario by remember { mutableStateOf(false) }
    var motivoReporte by remember { mutableStateOf("Ya pagué por otro medio") }
    var comentarioReporte by remember { mutableStateOf("") }
    var errorReporte by remember { mutableStateOf(false) }

    val estaPagado = compromiso.estado == EstadoCompromiso.PAGADO

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del compromiso") },
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
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
        ) {
            Card(
                shape = EstilosTarjeta.forma,
                colors = EstilosTarjeta.colores(),
                elevation = EstilosTarjeta.elevacion(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(Dimens.espacioMedio),
                    verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)
                ) {
                    FilaDato("Empresa", nombreEmpresa)
                    FilaDato("Concepto", textoConcepto(compromiso))
                    FilaDato("Folio", compromiso.folio)
                    FilaDato("Vencimiento", formatearFecha(compromiso.fechaVencimiento))
                    FilaDato("Monto", formatearPesos(compromiso.monto))

                    if (diasAtraso > 0) {
                        FilaDato("Días de atraso", if (diasAtraso == 1) "1 día" else "$diasAtraso días")
                    }

                    if (recargo > 0) {
                        FilaDato("Recargo por atraso", formatearPesos(recargo))
                    }
                    FilaDato(
                        etiqueta = if (estaPagado) "Total pagado" else "Total a pagar",
                        valor = formatearPesos(total),
                        destacado = true
                    )
                    FilaDato(
                        "Estado",
                        compromiso.estado.etiqueta,
                        colorValor = colorDeEstado(compromiso.estado)
                    )
                }
            }

            if (motivoNoPago != null) {
                Text(
                    text = textoMotivo(motivoNoPago, anterior),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            if (estaPagado) {
                BotonSecundario(
                    texto = "Ver comprobante",
                    onClick = onVerComprobante,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                BotonPrincipal(
                    texto = "Pagar",
                    onClick = { mostrarConfirmacion = true },
                    habilitado = motivoNoPago == null,
                    modifier = Modifier.fillMaxWidth()
                )
            }


            if (
                compromiso.estado == EstadoCompromiso.PENDIENTE ||
                compromiso.estado == EstadoCompromiso.VENCIDO
            ) {
                BotonSecundario(
                    texto = "Reportar un problema",
                    onClick = {
                        motivoReporte = "Ya pagué por otro medio"
                        comentarioReporte = ""
                        errorReporte = false
                        mostrarFormulario = true
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (
                (compromiso.estado == EstadoCompromiso.PENDIENTE ||
                        compromiso.estado == EstadoCompromiso.VENCIDO) &&
                !compromiso.fechaVencimiento.isBefore(LocalDate.now())
            ) {
                BotonSecundario(
                    texto = "Agregar al calendario",
                    onClick = {
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

                            putExtra(
                                CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                                inicio
                            )
                            putExtra(
                                CalendarContract.EXTRA_EVENT_END_TIME,
                                fin
                            )
                            putExtra(
                                CalendarContract.EXTRA_EVENT_ALL_DAY,
                                true
                            )
                            putExtra(
                                CalendarContract.Events.TITLE,
                                "PagoFlex - ${textoConcepto(compromiso)}"
                            )
                            putExtra(
                                CalendarContract.Events.DESCRIPTION,
                                "Vencimiento con $nombreEmpresa. " +
                                        "Folio: ${compromiso.folio}. " +
                                        "Consulta PagoFlex para revisar tu compromiso."
                            )
                        }

                        try {
                            context.startActivity(intent)
                        } catch (e: ActivityNotFoundException) {
                            Toast.makeText(
                                context,
                                "No hay una aplicación de calendario disponible.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (reporteActual != null) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(Dimens.espacioMedio),
                        verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)
                    ) {
                        Text(
                            "Seguimiento del reporte",
                            style = MaterialTheme.typography.titleMedium
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

        }
    }

    if (mostrarConfirmacion) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmacion = false },
            title = { Text("Confirmar pago") },
            text = { Text("Vas a pagar ${formatearPesos(total)} a $nombreEmpresa.") },
            confirmButton = {
                TextButton(onClick = {
                    mostrarConfirmacion = false
                    onConfirmarPago()
                }) { Text("Confirmar pago") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarConfirmacion = false }) { Text("Cancelar") }
            }
        )
    }


    if (mostrarFormulario) {
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
                    enabled = motivoReporte != "Otro" ||
                            comentarioReporte.isNotBlank(),
                    onClick = {
                        val guardado = onCrearReporte(
                            motivoReporte,
                            comentarioReporte
                        )

                        if (guardado) {
                            mostrarFormulario = false
                        } else {
                            errorReporte = true
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
