
package cl.fintech.pagoflex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.fintech.pagoflex.util.Reconocimiento
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReconocimientosScreen(
    reconocimientos: List<Reconocimiento>,
    onVolver: () -> Unit
) {
    val formatoFecha = DateTimeFormatter.ofPattern("dd-MM-yyyy")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis reconocimientos") },
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
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    "Reconocemos tus avances por realizar pagos a tiempo.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            items(
                reconocimientos,
                key = { it.titulo }
            ) { reconocimiento ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (reconocimiento.obtenido) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                reconocimiento.titulo,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                if (reconocimiento.obtenido) {
                                    "Obtenido"
                                } else {
                                    "Pendiente"
                                },
                                style = MaterialTheme.typography.labelMedium
                            )
                        }

                        Text(reconocimiento.descripcion)

                        if (reconocimiento.obtenido) {
                            Text(
                                "Obtenido el ${
                                    reconocimiento.fechaObtenida
                                        ?.format(formatoFecha)
                                }",
                                style = MaterialTheme.typography.bodyMedium
                            )

                        } else {
                            val mensajePendiente = when {
                                reconocimiento.titulo == "Cumplimiento ejemplar" ->
                                    "Se obtiene con al menos un 95 % de cumplimiento y 3 compromisos evaluables."

                                reconocimiento.faltantes == 1 ->
                                    "Te falta 1 pago a tiempo."

                                else ->
                                    "Te faltan ${reconocimiento.faltantes} pagos a tiempo."
                            }

                            Text(
                                mensajePendiente,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                    }
                }
            }

            item {
                Text(
                    "Los reconocimientos obtenidos se mantienen aunque después tengas un atraso.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
