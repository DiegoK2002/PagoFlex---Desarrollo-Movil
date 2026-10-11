package cl.app.pagoflex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import cl.app.pagoflex.R
import cl.app.pagoflex.model.Pago
import cl.app.pagoflex.ui.components.FilaDato
import cl.app.pagoflex.ui.styles.EstilosBoton
import cl.app.pagoflex.ui.styles.EstilosTarjeta
import cl.app.pagoflex.ui.theme.ColoresEstado
import cl.app.pagoflex.ui.theme.Dimens
import cl.app.pagoflex.util.formatearFechaHora
import cl.app.pagoflex.util.formatearPesos

// =====================================================================
//  ComprobanteScreen.kt  —  COMPROBANTE DE PAGO (RF-06, RN-05)
//  ---------------------------------------------------------------------
//  Acredita que el pago se realizó. Solo se MUESTRA: no se modifica ni
//  se elimina. El RUT sale parcialmente oculto. Los datos del medio de
//  pago son solo el nombre del medio simulado (nunca tarjetas).
//  Recibe lo que debe dibujar y una lambda; no conoce el NavController.
// =====================================================================

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
                }
            )
        }
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
                stringResource(R.string.pago_realizado),
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
                    modifier = Modifier.padding(Dimens.espacioMedio),
                    verticalArrangement = Arrangement.spacedBy(Dimens.espacioChico)
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
                colors = EstilosBoton.colorPrincipal(),
                elevation = EstilosBoton.elevacion(),
                contentPadding = EstilosBoton.relleno,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.alturaBoton)
            ) {
                Text(
                    stringResource(R.string.btn_volver_compromisos),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}
