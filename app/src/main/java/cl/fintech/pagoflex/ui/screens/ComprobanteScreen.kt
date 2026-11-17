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
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cl.fintech.pagoflex.data.remote.PagoRemoto
import cl.fintech.pagoflex.ui.components.BotonPrincipal
import cl.fintech.pagoflex.ui.components.FilaDato
import cl.fintech.pagoflex.ui.styles.EstilosTarjeta
import cl.fintech.pagoflex.ui.theme.ColoresEstado
import cl.fintech.pagoflex.ui.theme.Dimens
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(Dimens.espacioPantalla),
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
        ) {

            Text(
                if (confirmado) "¡Pago realizado!" else "Pago registrado, pendiente de confirmación",
                style = MaterialTheme.typography.titleLarge,
                color = if (confirmado) ColoresEstado.Pagado else MaterialTheme.colorScheme.onSurface
            )


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
                    FilaDato("N° de comprobante", numeroComprobante)
                    FilaDato("Fecha de pago", fecha)
                    FilaDato("Empresa", nombreEmpresa)
                    FilaDato("Folio", folio)
                    FilaDato("Concepto", concepto)
                    FilaDato("Monto pagado", "$${formatoMonto.format(pago.monto)}", destacado = true)
                    FilaDato("Medio de pago", pago.metodoPago)
                    FilaDato("Canal", "PagoFlex")
                    FilaDato("Pagado por", nombrePagador)
                    FilaDato("RUT", rutParcial)
                }
            }

            BotonPrincipal(
                texto = "Volver",
                onClick = onVolver,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
