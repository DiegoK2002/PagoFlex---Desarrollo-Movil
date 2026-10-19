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
import cl.fintech.pagoflex.model.Pago
import cl.fintech.pagoflex.ui.components.BotonPrincipal
import cl.fintech.pagoflex.ui.components.FilaDato
import cl.fintech.pagoflex.ui.styles.EstilosTarjeta
import cl.fintech.pagoflex.ui.theme.ColoresEstado
import cl.fintech.pagoflex.ui.theme.Dimens
import cl.fintech.pagoflex.util.formatearFechaHora
import cl.fintech.pagoflex.util.formatearPesos

/**
 * Comprobante de un pago realizado. Solo muestra los datos: no se puede modificar ni eliminar.
 * El RUT llega ya parcialmente oculto y el medio de pago es solo un nombre simulado.
 *
 * @param nombrePagador nombre de la persona que pago.
 * @param rutParcial RUT con la parte central oculta, por ejemplo "15.***.***-3".
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComprobanteScreen(
    pago: Pago,
    nombreEmpresa: String,
    nombrePagador: String,
    rutParcial: String,
    onVolver: () -> Unit
) {
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
            // Mensaje de confirmacion en verde
            Text(
                "¡Pago realizado!",
                style = MaterialTheme.typography.titleLarge,
                color = ColoresEstado.Pagado
            )

            // Datos del comprobante
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
                    FilaDato("N° de comprobante", pago.numeroComprobante)
                    FilaDato("Fecha y hora", formatearFechaHora(pago.fechaHora))
                    FilaDato("Empresa", nombreEmpresa)
                    FilaDato("Folio", pago.folioCompromiso)
                    FilaDato("Concepto", pago.concepto)
                    FilaDato("Monto pagado", formatearPesos(pago.montoPagado), destacado = true)
                    FilaDato("Medio de pago", pago.medioPago)
                    FilaDato("Canal", pago.canal)
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
