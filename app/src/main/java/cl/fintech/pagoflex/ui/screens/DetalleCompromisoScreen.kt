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

/**
 * Detalle de un compromiso y pago. Si esta vencido muestra los dias de atraso y el recargo;
 * si ya esta pagado muestra lo que se pago y permite ver el comprobante.
 * El boton Pagar pide confirmacion y se desactiva, explicando el motivo, cuando no se puede pagar.
 * La decision de si se puede pagar la toma el ViewModel; esta pantalla solo muestra el resultado.
 *
 * @param diasAtraso dias de atraso (0 si no esta vencido).
 * @param recargo recargo por atraso en pesos (0 si no corresponde).
 * @param total importe del compromiso: monto mas recargo, o lo pagado si ya esta pagado.
 * @param motivoNoPago razon por la que no se puede pagar, o null si se puede.
 * @param anterior compromiso anterior sin pagar, cuando es el motivo del bloqueo.
 */
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
    onVerComprobante: () -> Unit
) {
    // Indica si se muestra la ventana de confirmacion del pago
    var mostrarConfirmacion by remember { mutableStateOf(false) }

    val estaPagado = compromiso.estado == EstadoCompromiso.PAGADO

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
            // Datos del compromiso
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
                    // Los dias de atraso solo se muestran mientras el compromiso esta vencido
                    if (diasAtraso > 0) {
                        FilaDato("Días de atraso", if (diasAtraso == 1) "1 día" else "$diasAtraso días")
                    }
                    // El recargo se muestra si esta vencido o si ya se pago con recargo
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

            // Explicacion de por que no se puede pagar
            if (motivoNoPago != null) {
                Text(
                    text = textoMotivo(motivoNoPago, anterior),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            // Un compromiso pagado ofrece ver su comprobante; los demas ofrecen pagar
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
        }
    }

    // Ventana de confirmacion: permite cancelar antes de pagar
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
}

/** Mensaje para la persona segun el motivo por el que no se puede pagar. */
private fun textoMotivo(motivo: MotivoNoPago, anterior: Compromiso?): String {
    val nombreAnterior = if (anterior != null) textoConcepto(anterior) else "el compromiso anterior"
    return when (motivo) {
        MotivoNoPago.YA_PAGADO -> "Este compromiso ya está pagado."
        MotivoNoPago.EN_REVISION -> "Este compromiso está en revisión por un reporte. Podrás pagarlo cuando se resuelva."
        MotivoNoPago.ANULADO -> "Este compromiso fue anulado, por lo que no tienes que pagarlo."
        MotivoNoPago.HAY_ANTERIOR -> "Primero debes pagar $nombreAnterior, que vence antes."
    }
}
