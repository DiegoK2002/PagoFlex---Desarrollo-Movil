package cl.app.pagoflex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cl.fintech.pagoflex.R
import cl.fintech.pagoflex.model.Pago
import cl.fintech.pagoflex.ui.components.FilaDato
import cl.fintech.pagoflex.ui.styles.EstilosBoton
import cl.fintech.pagoflex.ui.styles.EstilosTarjeta
import cl.fintech.pagoflex.ui.theme.ColoresEstado
import cl.fintech.pagoflex.util.formatearFechaHora
import cl.fintech.pagoflex.util.formatearPesos

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
                title = { Text(stringResource(R.string.titulo_comprobante)) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            painter = painterResource(R.drawable.ic_volver),
                            contentDescription = stringResource(R.string.cd_volver)
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.pago_realizado),
                style = MaterialTheme.typography.titleLarge,
                color = ColoresEstado.Pagado
            )

            Card(
                shape = EstilosTarjeta.forma,
                colors = EstilosTarjeta.colores(),
                elevation = EstilosTarjeta.elevacion(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilaDato(stringResource(R.string.dato_comprobante), pago.numeroComprobante)
                    FilaDato(stringResource(R.string.dato_fecha_hora), formatearFechaHora(pago.fechaHora))
                    FilaDato(stringResource(R.string.dato_empresa), nombreEmpresa)
                    FilaDato(stringResource(R.string.dato_folio), pago.folioCompromiso)
                    FilaDato(stringResource(R.string.dato_concepto), pago.concepto)
                    FilaDato(
                        stringResource(R.string.dato_monto_pagado),
                        formatearPesos(pago.montoPagado),
                        destacado = true
                    )
                    FilaDato(stringResource(R.string.dato_medio), pago.medioPago)
                    FilaDato(stringResource(R.string.dato_canal), pago.canal)
                    FilaDato(stringResource(R.string.dato_pagador), nombrePagador)
                    FilaDato(stringResource(R.string.dato_rut), rutParcial)
                }
            }

            Button(
                onClick = onVolver,
                shape = EstilosBoton.forma,
                colors = EstilosBoton.coloresPrincipal(),
                elevation = EstilosBoton.elevacion(),
                contentPadding = EstilosBoton.relleno,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = stringResource(R.string.btn_volver_compromisos),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}