package cl.fintech.pagoflex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cl.fintech.pagoflex.R
import cl.fintech.pagoflex.model.Compromiso
import cl.fintech.pagoflex.model.MotivoNoPago
import cl.fintech.pagoflex.ui.components.FilaDato
import cl.fintech.pagoflex.ui.components.colorDeEstado
import cl.fintech.pagoflex.ui.components.textoConcepto
import cl.fintech.pagoflex.ui.styles.EstilosBoton
import cl.fintech.pagoflex.ui.styles.EstilosTarjeta
import cl.fintech.pagoflex.util.formatearFecha
import cl.fintech.pagoflex.util.formatearPesos

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
    onConfirmarPago: () -> Unit
) {
    var mostrarConfirmacion by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.titulo_detalle)) },
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
                    FilaDato(stringResource(R.string.dato_empresa), nombreEmpresa)
                    FilaDato(stringResource(R.string.dato_concepto), textoConcepto(compromiso))
                    FilaDato(stringResource(R.string.dato_folio), compromiso.folio)
                    FilaDato(
                        stringResource(R.string.dato_vencimiento),
                        formatearFecha(compromiso.fechaVencimiento)
                    )
                    FilaDato(stringResource(R.string.dato_monto), formatearPesos(compromiso.monto))

                    if (diasAtraso > 0) {
                        FilaDato(
                            stringResource(R.string.dato_dias_atraso),
                            if (diasAtraso == 1) stringResource(R.string.dias_uno)
                            else stringResource(R.string.dias_varios, diasAtraso)
                        )
                        FilaDato(stringResource(R.string.dato_recargo), formatearPesos(recargo))
                    }
                    FilaDato(
                        stringResource(R.string.dato_total),
                        formatearPesos(total),
                        destacado = true
                    )
                    FilaDato(
                        stringResource(R.string.dato_estado),
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

            Button(
                onClick = { mostrarConfirmacion = true },
                enabled = motivoNoPago == null,
                shape = EstilosBoton.forma,
                colors = EstilosBoton.coloresPrincipal(),
                elevation = EstilosBoton.elevacion(),
                contentPadding = EstilosBoton.relleno,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(stringResource(R.string.btn_pagar), style = MaterialTheme.typography.labelLarge)
            }
        }
    }

    if (mostrarConfirmacion) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmacion = false },
            title = { Text(stringResource(R.string.titulo_confirmar_pago)) },
            text = {
                Text(stringResource(R.string.texto_confirmar_pago, formatearPesos(total), nombreEmpresa))
            },
            confirmButton = {
                TextButton(onClick = {
                    mostrarConfirmacion = false
                    onConfirmarPago()
                }) {
                    Text(stringResource(R.string.btn_confirmar_pago))
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarConfirmacion = false }) {
                    Text(stringResource(R.string.btn_cancelar))
                }
            }
        )
    }
}

@Composable
private fun textoMotivo(motivo: MotivoNoPago, anterior: Compromiso?): String = when (motivo) {
    MotivoNoPago.YA_PAGADO -> stringResource(R.string.motivo_pagado)
    MotivoNoPago.EN_REVISION -> stringResource(R.string.motivo_en_revision)
    MotivoNoPago.ANULADO -> stringResource(R.string.motivo_anulado)
    MotivoNoPago.HAY_ANTERIOR ->
        stringResource(R.string.motivo_orden, if (anterior != null) textoConcepto(anterior) else "")
}